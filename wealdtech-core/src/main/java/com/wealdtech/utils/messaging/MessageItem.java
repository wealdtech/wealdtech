package com.wealdtech.utils.messaging;

public class MessageItem
{
  private transient final int msgtype;
  private transient final String destination;
  private transient final MessageObjects<?> objects;

  public MessageItem(final int msgtype, final String destination, final MessageObjects<?> objects)
  {
    this.msgtype = msgtype;
    this.destination = destination;
    this.objects = objects;
  }

  public int getMsgType()
  {
    return this.msgtype;
  }
  
  public String getDestination()
  {
    return this.destination;
  }

  public MessageObjects<?> getObjects()
  {
    return this.objects;
  }
}
