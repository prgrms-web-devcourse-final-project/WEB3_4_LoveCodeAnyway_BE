package com.ddobang.backend.domain.store.exception;

import com.ddobang.backend.global.exception.ServiceException;
import com.ddobang.backend.global.exception.ErrorCode;

public class StoreException extends ServiceException {
  public StoreException(ErrorCode errorCode) {
    super(errorCode);
  }
}
