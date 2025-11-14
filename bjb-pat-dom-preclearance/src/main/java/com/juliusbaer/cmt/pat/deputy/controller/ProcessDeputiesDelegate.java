package com.juliusbaer.cmt.pat.deputy.controller;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("processDeputiesDelegate")
public class ProcessDeputiesDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        Object raw = execution.getVariable("deputiesList");
        List<?> list = (raw instanceof List) ? (List<?>) raw : Collections.emptyList();

        List<String> names = list.stream()
                .filter(Objects::nonNull)
                .map(this::invokeGetValue)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        execution.setVariable("deputiesNames", names.isEmpty() ? "" : String.join(", ", names));
    }

    private String invokeGetValue(Object item) {
        try {
            var m = item.getClass().getMethod("getValue", String.class, Class.class);
            Object v = m.invoke(item, "displayNameDeputy", String.class);
            return (v == null) ? null : v.toString();
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}