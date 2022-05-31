/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.wendy512.easyboot.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.HibernateValidator;


/**
 * 手动触发hibernate validator
 * @author wendy512
 * @date 2022-05-23 19:20:14
 * @since 1.0.0
 */
public final class ValidationUtils {

    private static final Validator VALIDATOR = Validation.byProvider(HibernateValidator.class).configure()
            .failFast(true).buildValidatorFactory().getValidator();
    
    private ValidationUtils() {}
    
    public static <T> void validate(T entity) throws ValidationException {
        Set<ConstraintViolation<T>> validateResult = VALIDATOR.validate(entity);
        if (null != validateResult && !validateResult.isEmpty()) {
            ConstraintViolation<T> violation = validateResult.iterator().next();
            throw new ValidationException(violation.getMessage());
        }
    }
}
