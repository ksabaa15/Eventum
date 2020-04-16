package ch.epfl.sdp.db;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static ch.epfl.sdp.ObjectUtils.verifyNotNull;

public abstract class DatabaseObjectBuilder<T> {

    private final List<String> mRequiredFields;

    protected DatabaseObjectBuilder(String... requiredFields) {
        mRequiredFields = Arrays.asList(requiredFields);
    }

    public abstract boolean hasLocation();
    @Nullable
    public abstract T buildFromMap(@NonNull Map<String, Object> data);

    @Nullable
    public abstract Map<String, Object> serializeToMap(@NonNull T object);

    @Nullable
    public abstract LatLng getLocation(@NonNull T object);

    protected void checkRequiredFields(Map<String, Object> data) {
        for(String field: mRequiredFields) {
            if(!data.containsKey(field)) {
                throw new IllegalArgumentException("Missing field in database map: " + field);
            }
        }
    }
}
