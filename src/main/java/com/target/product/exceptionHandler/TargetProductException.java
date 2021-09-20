package com.target.targetProject.exceptionHandler;

import org.apache.kafka.common.protocol.types.Field;

public class TargetProductException extends RuntimeException{
    public TargetProductException(String error){
        super(error);
    }
}
