package ch.epfl.sdp.ui.main.map;

import androidx.lifecycle.LiveData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import ch.epfl.sdp.Event;
import ch.epfl.sdp.db.Database;
import ch.epfl.sdp.db.queries.CollectionQuery;
import ch.epfl.sdp.map.MapManager;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapViewModelTest {

    @Mock
    private Database mDatabase;

    @Mock
    private LiveData<List<Event>> mEventsLive;

    @Mock
    private MapManager mMapManager;

    @Mock
    private CollectionQuery mCollectionQuery;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void MapViewModel_Constructor_DoTheRightQuery() {
        when(mDatabase.query(anyString())).thenReturn(mCollectionQuery);
        MapViewModel vm = new MapViewModel(mDatabase, mMapManager);
        verify(mDatabase).query("events");
    }


}
