package com.graze17;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WakeupAndSynchronizeReceiver extends BroadcastReceiver
{
  @Override
  public void onReceive(Context c, Intent intent)
  {
    PL.log(WakeupAndSynchronizeReceiver.class.getSimpleName() + ".onReceive() called.", c);

    Context applicationContext = c.getApplicationContext();
    EntryManager entryManager = EntryManager.getInstance(applicationContext);
    NewsRobScheduler scheduler = entryManager.getScheduler();

    if (entryManager.needsSession())
      scheduler.setNeedsSynchronizationNotification();
    else
    {
      boolean uploadOnly = SynchronizationService.ACTION_SYNC_UPLOAD_ONLY.equals(intent.getAction());
      Intent i = new Intent(c, SynchronizationService.class);
      if (uploadOnly)
        i.setAction(SynchronizationService.ACTION_SYNC_UPLOAD_ONLY);
      else
        scheduler.updateNextSyncTime(-1);
      SynchronizationService.acquireWakeLock(c);
      try
      {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
          c.startForegroundService(i);
        }
        else
        {
          c.startService(i);
        }
      }
      catch (IllegalStateException e)
      {
        PL.log("Unable to start SynchronizationService from receiver.", e, c);
      }
    }
  }

}
