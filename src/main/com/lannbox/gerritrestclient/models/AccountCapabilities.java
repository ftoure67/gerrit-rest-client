package com.lannbox.gerritrestclient.models;

import com.google.gson.*;
import com.google.gson.annotations.Since;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Since(2.5)
@SuppressWarnings("unused")
public class AccountCapabilities {
    transient final public Set<String> capabilities = new HashSet<String>();

    public QueryLimit queryLimit;

    private AccountCapabilities() {}

    public static class QueryLimit {
        public int min = -1;
        public int max = -1;

        private QueryLimit() {}
    }

    public static GsonBuilder registerGsonAdapter(GsonBuilder builder, double version) {
        return builder.registerTypeAdapter(AccountCapabilities.class, new AccountCapabilitiesDeserializer());
    }

    private static class AccountCapabilitiesDeserializer implements JsonDeserializer<AccountCapabilities> {
        private static class Placeholder extends AccountCapabilities {}

        @Override
        public AccountCapabilities deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // We deserialize a subclass to prevent this method from calling itself recursively
            AccountCapabilities accountCapabilities = context.deserialize(json, Placeholder.class);

            // Add all keys with value == true to capabilities set
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
                    if (primitive.isBoolean() && primitive.getAsBoolean()) {
                        accountCapabilities.capabilities.add(entry.getKey());
                    }
                }
            }

            return accountCapabilities;
        }
    }
}

