package ch.epfl.sdp.mocks;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import ch.epfl.sdp.Event;
import ch.epfl.sdp.db.queries.CollectionQuery;
import ch.epfl.sdp.db.queries.DocumentQuery;

public class MockDocumentQuery implements DocumentQuery {

    @Override
    public CollectionQuery collection(String collection) {
        return null;
    }

    @Override
    public void exists(@NonNull OnQueryCompleteCallback<Boolean> callback) {}

    @Override
    public <T> void get(@NonNull Class<T> type, @NonNull OnQueryCompleteCallback<T> callback) {}

    @Override
    public <T> void set(@NonNull T object, @NonNull OnQueryCompleteCallback<Void> callback) {}

    @Override
    public void update(@NonNull String field, @NonNull Object value, @NonNull OnQueryCompleteCallback<Void> callback) { }

    @Override
    public <T> LiveData<T> livedata(@NonNull Class<T> type) {
        if (type == Event.class) {
            return (LiveData<T>)new MockEventLiveData();
        }
        return null;
    }



    @Override
    public void delete(@NonNull OnQueryCompleteCallback<Void> callback) {}
}
