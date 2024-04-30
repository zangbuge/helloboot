package com.hugmount.helloboot;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

/**
 * @author lhm
 * @date 2024/4/30
 */
public class AbstractMockTest {

    public <T, R, Children extends AbstractWrapper<T, R, Children>> AbstractWrapper<T, R, Children>
    mpWrap(AbstractWrapper<T, R, Children> mock) {

        ArgumentMatcher<AbstractWrapper<T, R, Children>> argumentMatcher = new Eq<>(mock);
        return Mockito.argThat(argumentMatcher);
    }

    private class Eq<T, R, Children extends AbstractWrapper<T, R, Children>>
            implements ArgumentMatcher<AbstractWrapper<T, R, Children>> {

        private AbstractWrapper<T, R, Children> mock;

        public Eq(AbstractWrapper<T, R, Children> mock) {
            this.mock = mock;
        }

        @Override
        public boolean matches(AbstractWrapper<T, R, Children> value) {
            if (!value.getSqlSegment().equals(mock.getSqlSegment())) {
                return false;
            }
            if (!value.getParamNameValuePairs().toString().equals(mock.getParamNameValuePairs().toString())) {
                return false;
            }
            return true;
        }
    }

}
