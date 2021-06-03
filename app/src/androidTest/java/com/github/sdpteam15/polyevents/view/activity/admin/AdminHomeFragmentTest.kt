package com.github.sdpteam15.polyevents.view.activity.admin

/*
@RunWith(MockitoJUnitRunner::class)
class AdminHubFragmentTest {
var mainActivity = ActivityScenarioRule(MainActivity::class.java)

lateinit var testUser: UserEntity

val uid = "testUid"
val username = "JohnDoe"
val email = "John@Doe.com"

@Before
fun setup() {
    val mockedDatabase = HelperTestFunction.defaultMockDatabase()
    Database.currentDatabase = mockedDatabase

    UserLogin.currentUserLogin.signOut()
    testUser = UserEntity(
        uid = uid,
        username = username,
        email = email
    )
    MainActivity.currentUser = testUser
    MainActivity.currentUserObservable = Observable(testUser)
    PolyEventsApplication.inTest = true

    val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
    ActivityScenario.launch<MainActivity>(intent)

    Espresso.onView(ViewMatchers.withId(R.id.ic_home)).perform(click())
    Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_admin))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Intents.init()
}

@After
fun teardown() {
    MainActivity.currentUser = null

    Intents.release()
    Database.currentDatabase = FirestoreDatabaseProvider
}

 @Test
 fun clickOnBtnItemRequestManagementDisplayCorrectActivity() {
     Database.currentDatabase = FakeDatabase
     Espresso.onView(ViewMatchers.withId(R.id.btnRedirectItemReqManagement)).perform(click())
     Intents.intended(IntentMatchers.hasComponent(ItemRequestManagementActivity::class.java.name))
 }

 @Test
 fun clickOnBtnZoneManagementDisplayCorrectActivity() {
     Espresso.onView(ViewMatchers.withId(R.id.btnRedirectZoneManagement)).perform(click())
     Intents.intended(IntentMatchers.hasComponent(ZoneManagementListActivity::class.java.name))
 }

 @Test
 fun clickOnBtnEventDisplayCorrectActivity() {
     val mockedDatabase = HelperTestFunction.defaultMockDatabase()
     Database.currentDatabase = mockedDatabase
     val mockedEventDatabase = mock(EventDatabaseInterface::class.java)
     Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(mockedEventDatabase)

     Mockito.`when`(
         mockedEventDatabase.getEvents(
             anyOrNull(),
             anyOrNull(),
             anyOrNull()
         )
     ).thenReturn(
         Observable(true)
     )
     Espresso.onView(ViewMatchers.withId(R.id.btnRedirectEventManager)).perform(click())
     Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
 }

 @Test
 fun clickOnBtnUserManagementDisplayCorrectActivity() {
     Database.currentDatabase = FakeDatabase
     Espresso.onView(ViewMatchers.withId(R.id.btnRedirectUserManagement)).perform(click())
     Intents.intended(IntentMatchers.hasComponent(UserManagementListActivity::class.java.name))
 }


 @Test
 fun itemRequestActivityOpensOnClick() {
     Espresso.onView(ViewMatchers.withId(R.id.ic_more)).perform(click())

     Espresso.onView(ViewMatchers.withId(R.id.btnRedirectItemReqManagement)).perform(click())
     Intents.intended(IntentMatchers.hasComponent(ItemRequestActivity::class.java.name))
 }

 @Test
 fun itemsAdminActivity() {
     var availableItems: MutableMap<Item, Int> = mutableMapOf()
     availableItems[Item(null, "Chocolat", "OTHER")] = 30
     availableItems[Item(null, "Kiwis", "OTHER")] = 10
     availableItems[Item(null, "230V Plugs", "PLUG")] = 30
     availableItems[Item(null, "Fridge (large)", "OTHER")] = 5
     availableItems[Item(null, "Cord rewinder (15m)", "PLUG")] = 30
     availableItems[Item(null, "Cord rewinder (50m)", "PLUG")] = 10
     availableItems[Item(null, "Cord rewinder (25m)", "PLUG")] = 20

     Database.currentDatabase = FakeDatabase

     FakeDatabaseItem.items.clear()
     for ((item, count) in availableItems) {
         Database.currentDatabase.itemDatabase!!.createItem(item, count)
     }

     Espresso.onView(ViewMatchers.withId(R.id.ic_more)).perform(click())
     Espresso.onView(ViewMatchers.withId(R.id.btn_admin_items_list)).perform(click())
     Intents.intended(IntentMatchers.hasComponent(ItemsAdminActivity::class.java.name))
 }

}     */