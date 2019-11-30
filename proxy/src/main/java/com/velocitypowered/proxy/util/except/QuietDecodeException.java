package com.velocitypowered.proxy.util.except;

import io.netty.handler.codec.DecoderException;

public class QuietDecodeException extends DecoderException {

  public QuietDecodeException() {
  }

  public QuietDecodeException(String message, Throwable cause) {
    super(message, cause);
  }

  public QuietDecodeException(String message) {
    super(message);
  }

  public QuietDecodeException(Throwable cause) {
    super(cause);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
