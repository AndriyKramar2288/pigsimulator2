package com.banew.utilites;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Reference<T> {
    private T element;
}
