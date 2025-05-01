package com.ddobang.backend.domain.store.exception;

import com.ddobang.backend.global.exception.ServiceException;

public class StoreException extends ServiceException {
  public StoreException(StoreErrorCode errorCode) {
    super(errorCode);
  }
}
