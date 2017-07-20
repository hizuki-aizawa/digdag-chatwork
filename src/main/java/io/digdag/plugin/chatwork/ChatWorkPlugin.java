package io.digdag.plugin.chatwork;

import io.digdag.spi.OperatorFactory;
import io.digdag.spi.OperatorProvider;
import io.digdag.spi.Plugin;
import io.digdag.spi.TemplateEngine;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

public class ChatWorkPlugin
        implements Plugin
{
    @Override
    public <T> Class<? extends T> getServiceProvider(Class<T> type)
    {
        if (type == OperatorProvider.class) {
            return ChatWorkOperatorProvider.class.asSubclass(type);
        }
        else {
            return null;
        }
    }

    public static class ChatWorkOperatorProvider
            implements OperatorProvider
    {
        @Inject
        protected TemplateEngine templateEngine;

        @Override
        public List<OperatorFactory> get()
        {
            return Arrays.asList(
                    new ChatWorkOperatorFactory(templateEngine));
        }
    }
}
