
package ru.kkuzmichev.simpleappforespresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

@RunWith(AndroidJUnit4.class)
public class IntentAndIdlingTests {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResources() {
        IdlingRegistry.getInstance().register(EspressoIdlingResources.idlingResource);
    }

    @After
    public void unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResources.idlingResource);
    }

    //Задание 1 Реализация теста на проверку Intent
    @Test
    public void intentTest() {
        Intents.init();
        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("Еще"),
                        withParent(withParent(withId(R.id.toolbar)))));
        overflowMenuButton.check(matches(isDisplayed()));
        overflowMenuButton.perform(click());
        ViewInteraction settingsButton = onView(
                allOf((withId(androidx.recyclerview.R.id.title)), withText("Settings")));
        settingsButton.check(matches(isDisplayed()));
        settingsButton.perform(click());
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_VIEW));
        Intents.intended(IntentMatchers.hasData("https://google.com"));
        Intents.release();
    }

    // Задание 2. Реализация теста с использованием Idling Resources + Доп.задания
    @Test
    public void testGalleryAndElements() {
        ViewInteraction appCompatImageButton = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.toolbar),
                                childAtPosition(
                                        withClassName(is("com.google.android.material.appbar.AppBarLayout")),
                                        0)),
                        1)));
        appCompatImageButton.check(matches(isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(withId(R.id.nav_gallery));
        navigationMenuItemView.check(matches(isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction recyclerView = onView(withId(R.id.recycle_view));
        recyclerView.check(matches(isDisplayed()));
        ViewInteraction textView = onView(allOf(withId(R.id.item_number), withText("7"),
                withParent(withParent(withId(R.id.recycle_view)))));
        textView.check(matches(isDisplayed()));
        recyclerView.check(CustomViewAssertions.isRecyclerView());
        recyclerView.check(matches(CustomViewMatcher.recyclerViewSizeMatcher(10)));
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
