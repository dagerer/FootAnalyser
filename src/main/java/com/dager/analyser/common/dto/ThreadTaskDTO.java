package com.dager.analyser.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.Future;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadTaskDTO {

    private Integer threadNum;

    private List<?> data;

    private List<Future<Object>> response;
}
