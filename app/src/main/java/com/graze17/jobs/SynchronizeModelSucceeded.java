package com.graze17.jobs;

public class SynchronizeModelSucceeded extends ModelUpdateResult
{
  int noOfEntriesUpdated = -1;
  int noOfEntriesFetched = -1;

  public SynchronizeModelSucceeded(int noOfNewEntriesUpdated, int noOfNewEntriesFetched)
  {
    this.noOfEntriesUpdated = noOfNewEntriesUpdated;
    this.noOfEntriesFetched = noOfNewEntriesFetched;
  }

  public int getNoOfEntriesUpdated()
  {
    return noOfEntriesUpdated;
  }

  public int getNoOfEntriesFetched()
  {
    return noOfEntriesFetched;
  }

  @Override
  public String getMessage()
  {
    return String.format("%s new entries downloaded.", noOfEntriesFetched);
  }
}
