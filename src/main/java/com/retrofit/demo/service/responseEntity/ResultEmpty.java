package com.retrofit.demo.service.responseEntity;

import lombok.*;

import java.io.Serializable;

/**
 * @author puthlive
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultEmpty implements Serializable {
    private int code;
    private String msg;
}
