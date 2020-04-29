package ch.epfl.sdp.ui.main.swipe;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.test.rule.ActivityTestRule;

import ch.epfl.sdp.Event;
import ch.epfl.sdp.EventBuilder;
import ch.epfl.sdp.R;
import ch.epfl.sdp.User;
import ch.epfl.sdp.auth.Authenticator;
import ch.epfl.sdp.auth.UserInfo;
import ch.epfl.sdp.db.Database;
import ch.epfl.sdp.db.DatabaseObject;
import ch.epfl.sdp.db.queries.CollectionQuery;
import ch.epfl.sdp.db.queries.DocumentQuery;
import ch.epfl.sdp.db.queries.FilterQuery;
import ch.epfl.sdp.db.queries.LocationQuery;
import ch.epfl.sdp.mocks.MockFragmentFactory;
import ch.epfl.sdp.mocks.MockLocationService;
import ch.epfl.sdp.ui.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SwipeFragmentTest {

    private static final String DUMMY_USERREF = "sdfkjghsdflkjghsdlfkgjh";
    private static final UserInfo DUMMY_USERINFO = new UserInfo(DUMMY_USERREF, "testname", "testemail");

    @Mock
    private Database mDatabase;

    @Mock
    private Authenticator mAuthenticator;

    @Mock
    private CollectionQuery mCollectionQuery;

    @Mock
    private DocumentQuery mDocumentQuery;

    @Mock
    private FilterQuery mFilterQuery;

    @Mock
    private LocationQuery mLocationQuery;

    private EventBuilder eventBuilder = new EventBuilder();
    private Event eventTest1 = eventBuilder.setTitle("title").setDescription("description").setDate("01/01/2020").build();
    private Event eventTest2 = eventBuilder.setTitle("title2").setDescription("description2").setDate("02/01/2020").build();

    private MutableLiveData<User> mUserLiveData = new MutableLiveData<>();
    private MutableLiveData<List<DatabaseObject<Event>>> mEventsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<DatabaseObject<Event>>> mAttendingEventsLiveData = new MutableLiveData<>();
    private MutableLiveData<Collection<DatabaseObject<Event>>> mLocationEventsLiveData = new MutableLiveData<>();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private void scenario() {
        when(mDatabase.query(anyString())).thenReturn(mCollectionQuery);
        when(mCollectionQuery.liveData(Event.class)).thenReturn(mEventsLiveData);
        when(mCollectionQuery.document(any())).thenReturn(mDocumentQuery);
        when(mCollectionQuery.whereArrayContains(anyString(), any())).thenReturn(mFilterQuery);
        when(mCollectionQuery.atLocation(any(), anyDouble())).thenReturn(mLocationQuery);
        when(mLocationQuery.liveData(Event.class)).thenReturn(mLocationEventsLiveData);
        when(mFilterQuery.liveData(Event.class)).thenReturn(mAttendingEventsLiveData);
        when(mDocumentQuery.livedata(User.class)).thenReturn(mUserLiveData);
        when(mAuthenticator.getCurrentUser()).thenReturn(DUMMY_USERINFO);

        FragmentScenario<SwipeFragment> scenario = FragmentScenario.launchInContainer(
                SwipeFragment.class,
                new Bundle(),
                R.style.Theme_AppCompat,
                new MockFragmentFactory(SwipeFragment.class, mDatabase, mAuthenticator, new MockLocationService()));
    }

    @Test
    public void SwipeFragment_NewCardIsShow() throws InterruptedException {
        scenario();

        List<DatabaseObject<Event>> events = new ArrayList<>();
        events.add(new DatabaseObject<>("sdfsdfsdfsdf", eventTest1));
        mEventsLiveData.postValue(events);

        Thread.sleep(1500);
        onView(withText("title")).check(matches(isDisplayed()));
    }

    @Test
    public void SwipeFragment_SwipeLeftAndRightRemoveCard() throws InterruptedException {
        scenario();

        List<DatabaseObject<Event>> events = new ArrayList<>();
        events.add(new DatabaseObject<>("3456734562436", eventTest1));
        mEventsLiveData.postValue(events);

        Thread.sleep(1500);
        onView(withText("title")).check(matches(isDisplayed()));
        onView(withId(R.id.cards_list_view)).perform(swipeLeft());
        Thread.sleep(1500);
        onView(withText("title")).check(doesNotExist());
    }

    @Test
    public void SwipeFragment_SecondCardIsShowAfterSwipe() throws InterruptedException {
        scenario();

        List<DatabaseObject<Event>> events = new ArrayList<>();
        events.add(new DatabaseObject<>("test1", eventTest1));
        events.add(new DatabaseObject<>("test2", eventTest2));
        mEventsLiveData.postValue(events);

        Thread.sleep(1500);
        onView(withText("title")).check(matches(isDisplayed()));
        onView(withId(R.id.cards_list_view)).perform(swipeRight());

        Thread.sleep(1500);
        onView(withText("title")).check(doesNotExist());
        onView(withText("title2")).check(matches(isDisplayed()));
    }

    @Test
    public void SwipeFragment_ClickSToDetailled() {
        scenario();

        List<DatabaseObject<Event>> events = new ArrayList<>();
        events.add(new DatabaseObject<>("3456734562436", eventTest1));
        mEventsLiveData.postValue(events);

        onView(withId(R.id.cards_list_view)).perform(click());

        onView(withId(R.id.default_event_layout)).check(matches(isDisplayed()));
    }

}

