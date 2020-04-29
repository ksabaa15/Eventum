package ch.epfl.sdp.ui.main.attending;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import ch.epfl.sdp.Event;
import ch.epfl.sdp.EventBuilder;
import ch.epfl.sdp.R;
import ch.epfl.sdp.auth.Authenticator;
import ch.epfl.sdp.auth.UserInfo;
import ch.epfl.sdp.db.Database;
import ch.epfl.sdp.db.DatabaseObject;
import ch.epfl.sdp.db.queries.CollectionQuery;
import ch.epfl.sdp.db.queries.FilterQuery;
import ch.epfl.sdp.mocks.MockFragmentFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttendingListFragmentTest {

    private static final String DUMMY_USERREF = "sdfkjghsdflkjghsdlfkgjh";
    private static final UserInfo DUMMY_USERINFO = new UserInfo(DUMMY_USERREF, "testname", "testemail");

    @Mock
    private Database mDatabase;

    @Mock
    private Authenticator mAuthenticator;

    @Mock
    private CollectionQuery mCollectionQuery;

    @Mock
    private FilterQuery mFilterQuery;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void AttendingListFragment() {
        MutableLiveData<List<DatabaseObject<Event>>> eventLiveData = new MutableLiveData<>();

        when(mAuthenticator.getCurrentUser()).thenReturn(DUMMY_USERINFO);
        when(mDatabase.query(anyString())).thenReturn(mCollectionQuery);
        when(mCollectionQuery.whereArrayContains(anyString(), any())).thenReturn(mFilterQuery);
        when(mFilterQuery.liveData(Event.class)).thenReturn(eventLiveData);

        FragmentScenario<AttendingListFragment> scenario = FragmentScenario.launchInContainer(
                AttendingListFragment.class,
                new Bundle(),
                R.style.Theme_AppCompat,
                new MockFragmentFactory(AttendingListFragment.class, mAuthenticator, mDatabase));

        List<DatabaseObject<Event>> events = new ArrayList<>();
        EventBuilder eventBuilder = new EventBuilder();
        Event event = eventBuilder.setTitle("testtitle").setDescription("description").setDate("01/01/2020").build();
        events.add(new DatabaseObject<>("asdfasdfasdf", event));
        eventLiveData.postValue(events);

        onView(withText("testtitle")).check(matches(isDisplayed()));
    }
}
