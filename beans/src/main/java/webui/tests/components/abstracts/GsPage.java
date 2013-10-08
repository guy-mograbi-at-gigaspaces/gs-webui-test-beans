package webui.tests.components.abstracts;

import com.thoughtworks.selenium.Selenium;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webui.tests.utils.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * User: guym
 * Date: 3/6/13
 * Time: 10:38 PM
 */
@Component
public abstract class GsPage<T extends GsPage> extends AbstractComponent<T> {

   @Autowired
    protected Selenium selenium;

    private static Logger logger = LoggerFactory.getLogger(GsPage.class);

    public boolean isTextPresent( String text ) {
        return selenium.isTextPresent( text );
    }

    @Autowired
    public GsPage<T> setSelenium( Selenium selenium ) {
        this.selenium = selenium;
        return this;
    }

    public WebElement findFirstDisplayedWindowDialog() {
        return CollectionUtils.first(findDisplayedWindowDialogs());
    }

    public Collection<WebElement> findDisplayedWindowDialogs() {
        return findDisplayed( By.cssSelector( ".x-window-dlg" ) );
    }

    public Collection<WebElement> findDisplayed( By by ) {
        List<WebElement> elements = new WebDriverWait(webDriver, 2).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
        Collection<WebElement> displayElements = CollectionUtils.select( elements, new Predicate() {
            @Override
            public boolean evaluate( Object o ) {
                return ( ( WebElement ) o ).isDisplayed();
            }
        } );
        return displayElements;
    }

    public T load() {
        load( webDriver );
        return ( T ) this;
    }

    public  T enterFrame(){
       return null;
    }

    // goes back to default content
    public T exitAllFrames(){
        switchManager.leaveAll();
        return (T) this;
    }

    public T exitFrame(){
        return null;
    }


    public boolean isTextInPopups(String containedText) {
        Collection<WebElement> popups = findDisplayedWindowDialogs();
        if ( !CollectionUtils.isEmpty( popups ) )
        {
            for ( WebElement popup : popups )
            {
                String text = popup.getText();
                if ( !StringUtils.isEmpty( text ) && text.toLowerCase(  ).contains( containedText.toLowerCase(  ) ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public T closeDialog( final String str ){
        WebElement button = waitForPredicate(
                new ExpectedCondition<WebElement>() {
                    @Override
                    public WebElement apply( WebDriver webDriver) {
                        WebElement e =  findFirstDisplayedWindowDialog();
                        if ( e != null ){
                            logger.info("found dialog. searching for button [{}]", str );
                            Collection<WebElement> buttons = e.findElements( By.cssSelector( "button" ) );
                            if (!CollectionUtils.isEmpty(buttons)) {
                                logger.info("I have [{}] buttons.", buttons.size());
                                for (WebElement button : buttons) {
                                    if (button.getText().equalsIgnoreCase(str)) {
                                        return button;
                                    }
                                }
                            }
                        }
                        return null;
                    }
                }
        );
         button.click();
        return (T) this;
    }

}
