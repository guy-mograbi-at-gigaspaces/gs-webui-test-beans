How to use this project
=======================


1. Add a maven dependency to your pom
        
        <dependency>
            <groupId>webui.tests</groupId>
            <artifactId>beans</artifactId>
            <version>9.5.0-SNAPSHOT</version>
        </dependency>


2. Add the following maven repository to your pom


        <repository>
            <id>openspaces</id>
            <url>http://maven-repository.openspaces.org</url>
        </repository>

3. Add a submodule to your git project (or simply make the files available)

        git submodule add https://github.com/guy-mograbi-at-gigaspaces/selenium-drivers.git src/test/resources/selenium-drivers

4. If you are not in a GIT project or you cannot add a submodule, you can simply  
   make sure the files are available under `src/test/resources/selenium-drivers`  
   This path is configurable. 

5. Start writing your test. You can use Spring to define your beans.  
   Here is an example without a Spring context

        public class MyTest {

            private static Logger logger = LoggerFactory.getLogger(MyTest.class);
            SeleniumDriverFactory factory = new SeleniumDriverFactory();

            @After
            public void after(){
                logger.info("closing web driver");
               factory.quit();
            }

            @Test
            public void exampleTest(){
                logger.info("starting test");
                WebDriver driver = factory.init().getDriver();
                driver.get("http://www.google.com");

            }
        }


6. But this is not cool enough.. To get the full power of our system, lets use Spring.

        @ContextConfiguration
        @RunWith(SpringJUnit4ClassRunner.class)
        public class MyTest {

            private static Logger logger = LoggerFactory.getLogger(MyTest.class);

            @Autowired
            public WebDriver webDriver;

            @Test
            public void exampleTest(){
                logger.info("starting test");
                webDriver.get("http://www.google.com");
            }
        }

  Which is shorter, and nicer, but still - not REALLY COOL.. So lets get even cooler.
  Selenium works best when using the [Page model](https://code.google.com/p/selenium/wiki/PageObjects).

7. This example uses a page.

        @Component
        public class GoogleSearch extends GsPage<GoogleSearch> {

            @OnLoad
            @FindBy(css="[name=q]")
            WebElement searchInput;

            @OnLoad
            @FindBy(css="button[aria-label='Google Search'], input[type='submit'],table>tbody>tr>td>table>tbody>tr>td>div>div>span>span>input")
            @FirstDisplayed
            WebElement searchButton;

            public void search( String term ){
                searchInput.sendKeys(term);
                searchButton.click();
            }

            public GoogleSearch goTo() {
                webDriver.get("http://www.google.com");
                return load();
            }
        }


        @Component
        public class GoogleSearchResults extends GsPage<GoogleSearchResults> {

            private static Logger logger = LoggerFactory.getLogger(GoogleSearchResults.class);

            @FindBy( css = "h3.r a")
            List<WebElement> searchResults;

            public void printResults(){
                waitForCollectionSize(searchResults, SizeCondition.gt( 0 ));
                logger.info("I have [{}] results", searchResults.size());
                for (WebElement searchResult : searchResults) {
                    logger.info(searchResult.getText());
                }
            }

        }


        @ContextConfiguration
        @RunWith(SpringJUnit4ClassRunner.class)
        public class MyTest {

            private static Logger logger = LoggerFactory.getLogger(MyTest.class);

            @Autowired
            public GoogleSearch searchPage;

            @Autowired
            public GoogleSearchResults results;



            @Test
            public void exampleTest(){
                logger.info("starting test");
                searchPage.goTo().search("gs-webui-test-beans github");
                logger.info("printing results");
                results.load().printResults();
            }
        }

    The above example has 2 pages - one for search and another for results.
    The test searches for "gs-webui-tet-beans github" in google, and lists the results.

    ###    GsPage class

    Most of the magic in this code is available due to the GsPage<T> class the pages inherit from.
    The GsPage class has a lot of useful functions like "wait" and "load".

    ###    @FirstDisplayed

    Another useful function of this library is the "@FirstDisplayed" annotation.
    In this test we used it for the submit WebElement.
    Google's search page is different if you view it in English, other language or if you have instant search activated.

    The @FirstDisplayed annotation allows you to use @FindBy with multiple selectors and it will return
    the first displayed WebElement that matches the selector.

    Using this method, you can build a page that will handle different versions of your website.



