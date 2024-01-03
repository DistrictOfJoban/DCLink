package com.lx862.vendorneutral.texts;

import com.lx862.vendorneutral.VendorNeutralComponent;
import org.apache.commons.lang3.NotImplementedException;

public class Message implements VendorNeutralComponent {
    public final String content;

    public Message(String content) {
        this.content = content;
    }

    @Override
    public <T> T toDiscord() {
        throw new NotImplementedException();
    }

    @Override
    public <T> T toRevolt() {
        throw new NotImplementedException();
    }
}
