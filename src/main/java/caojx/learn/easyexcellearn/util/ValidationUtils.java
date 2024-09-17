package caojx.learn.easyexcellearn.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * 数据校验工具类
 *
 * @author caojx created on 2023/2/21 15:23
 */
public class ValidationUtils {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> String validateObject(T obj) {
        StringBuilder result = new StringBuilder();
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation<T> cv : set) {
                result.append(cv.getMessage()).append(";");
            }
        }
        return result.toString();
    }

}
