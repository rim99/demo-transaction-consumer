package org.example.transaction.consumer.adapter;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class Helper {
    public static String getRequiredParam(String name, Map<String, List<String>> params, List<String> missingParams) {
        List<String> tmpList = params.get(name);
        if (Objects.isNull(tmpList) || tmpList.size() == 0) {
            missingParams.add(name);
            return null;
        } else {
            return tmpList.get(0);
        }
    }

    public static Optional<String> getOptionalParam(String name, Map<String, List<String>> params) {
        List<String> tmpList = params.get(name);
        if (Objects.isNull(tmpList) || tmpList.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(tmpList.get(0));
        }
    }
}
