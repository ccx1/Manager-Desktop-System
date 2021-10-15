package com.example.manager.result;

import com.example.manager.enums.HttpCode;
import lombok.Data;

@Data
public class CodeException extends RuntimeException {

    private int code;

    public CodeException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CodeException(HttpCode httpStatus) {
        super(httpStatus.getMsg());
        this.code = httpStatus.getCode();
    }


}
