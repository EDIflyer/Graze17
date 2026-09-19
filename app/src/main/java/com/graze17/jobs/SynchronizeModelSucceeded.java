package com.graze17.jobs;

public class SynchronizeModelSucceeded extends ModelUpdateResult
{
  int noOfEntriesUpdated = -1;
  int noOfEntriesFetched = -1;
  int noOfArticlesMarkedRead = 0;

  public SynchronizeModelSucceeded(int noOfNewEntriesUpdated, int noOfNewEntriesFetched, int noOfArticlesMarkedRead)
  {
    this.noOfEntriesUpdated = noOfNewEntriesUpdated;
    this.noOfEntriesFetched = noOfNewEntriesFetched;
    this.noOfArticlesMarkedRead = noOfArticlesMarkedRead;
  }

  public int getNoOfEntriesUpdated()
  {
    return noOfEntriesUpdated;
  }

  public int getNoOfEntriesFetched()
  {
    return noOfEntriesFetched;
  }

  public int getNoOfArticlesMarkedRead()
  {
    return noOfArticlesMarkedRead;
  }

  @Override
  public String getMessage()
  {
    return String.format("%s new entries downloaded.", noOfEntriesFetched);
  }
}
