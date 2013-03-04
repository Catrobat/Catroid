import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Dummy annotations to avoid IDE error messages. Import the annotations in all Java step-definitions.
 */
public class CucumberAnnotation {

    @Target(ElementType.METHOD)
    public @interface Given {
        String value() default "";
    }

    @Target(ElementType.METHOD)
    public @interface When {
        String value() default "";
    }

    @Target(ElementType.METHOD)
    public @interface Then {
        String value() default "";
    }

    @Target(ElementType.METHOD)
    public @interface And {
        String value() default "";
    }
}
