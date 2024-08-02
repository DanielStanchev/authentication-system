package com.tinqinacademy.authentication.api.exceptionmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorWrapper {
    private List<ErrorResponseInfo> errorResponseInfoList;
}
