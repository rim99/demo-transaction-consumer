package org.example.transaction.consumer.adapter;

import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import org.example.transaction.consumer.entity.mapper.AggregationItemListSerializer;
import org.example.transaction.consumer.port.AggregationItem;
import org.example.transaction.consumer.port.*;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import static java.time.temporal.ChronoField.*;

public class TransactionAggregationHttpAPI implements Service {

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    private static final DateTimeFormatter df = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .appendValue(HOUR_OF_DAY, 2)
            .appendValue(MINUTE_OF_HOUR, 2)
            .parseLenient()
            .toFormatter();

    private final TransactionAggregationHttpService transactionAggregationHttpService;
    private final AggregationItemListSerializer aggregationItemListSerializer;

    public TransactionAggregationHttpAPI(
            TransactionAggregationHttpService service,
            AggregationItemListSerializer aggregationItemListSerializer
    ) {
        this.transactionAggregationHttpService = service;
        this.aggregationItemListSerializer = aggregationItemListSerializer;
    }

    private void getAggregationHandler(ServerRequest serverRequest, ServerResponse serverResponse) {
        serverResponse.headers()
                .contentType(MediaType.APPLICATION_JSON);

        List<String> missingParams = new LinkedList<>();
        Map<String, List<String>> params = serverRequest.queryParams().toMap();

        String fromInText = Helper.getRequiredParam("from", params, missingParams);
        String toInText = Helper.getRequiredParam("to", params, missingParams);
        String timeframeInText = Helper.getRequiredParam("timeframe", params, missingParams);
        String typeInText = Helper.getRequiredParam("type", params, missingParams);
        if (missingParams.size() > 0) {
            JsonObject jsonErrorObject = JSON.createObjectBuilder()
                    .add("missing_params", JSON.createArrayBuilder(missingParams))
                    .build();
            serverResponse.status(Http.Status.BAD_REQUEST_400).send(jsonErrorObject);
            return;
        }

        LocalDateTime from = LocalDateTime.parse(
                fromInText, df);
        LocalDateTime to = LocalDateTime.parse(
                toInText, df);
        AggregationTimeFrame timeframe = AggregationTimeFrame.valueOf(
                timeframeInText.toUpperCase());
        AggregationType type = AggregationType.valueOf(
                typeInText.toUpperCase());
        Optional<Merchant> merchant = Helper.getOptionalParam("merchant", params)
                .map($ -> new Merchant(Integer.valueOf($)));
        Optional<PaymentType> transactionType = Helper.getOptionalParam("transaction_type", params)
                .map(PaymentType::getByLabel);
        Optional<PaymentVendor> vendor = Helper.getOptionalParam("vendor", params)
                .map(PaymentVendor::getByLabel);

        List<AggregationItem> aggregations = transactionAggregationHttpService
                .getAggregations(from, to, timeframe, type, merchant, transactionType, vendor);

        byte[] body = aggregationItemListSerializer.serialize(aggregations);
        serverResponse
                .status(Http.ResponseStatus.create(200))
                .send(body);
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::getAggregationHandler);
    }
}
