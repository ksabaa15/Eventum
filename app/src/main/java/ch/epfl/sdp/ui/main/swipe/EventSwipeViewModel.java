package ch.epfl.sdp.ui.main.swipe;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;

import java.util.Collection;
import java.util.List;

import ch.epfl.sdp.Event;
import ch.epfl.sdp.db.Database;
import ch.epfl.sdp.db.queries.CollectionQuery;
import ch.epfl.sdp.ui.ParameterizedViewModelFactory;

import static ch.epfl.sdp.ObjectUtils.verifyNotNull;

public class EventSwipeViewModel extends ViewModel {

    static class EventSwipeViewModelFactory extends ParameterizedViewModelFactory {

        EventSwipeViewModelFactory() {
            super(Database.class);
        }

        void setDatabase(@NonNull Database database) {
            setValue(0, verifyNotNull(database));
        }
    }

    private final CollectionQuery mSwipeQuery;
    private final Database mDatabase;

    private LiveData<Collection<Event>> mSwipeLiveData;

    public EventSwipeViewModel(@NonNull Database database) {
        mDatabase = database;
        mSwipeQuery = database.query("events");
    }

    public LiveData<Collection<Event>> getNewEvents(GeoPoint location, double radius) {
        if(mSwipeLiveData == null) {
            mSwipeLiveData = mSwipeQuery.atLocation(location, radius).liveData(Event.class);
        }
        return mSwipeLiveData;
    }

    public LiveData<Collection<Event>> getSwipeLiveData() {
        return mSwipeLiveData;
    }
}
