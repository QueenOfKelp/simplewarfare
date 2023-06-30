package queenofkelp.simplewarfare.test_code;

import queenofkelp.simplewarfare.gun.item.attachments.AbstractStatModifyingAttachmentItem;

public class TestStatAttachment extends AbstractStatModifyingAttachmentItem {
    public TestStatAttachment(Settings settings) {
        super(settings);
    }

    @Override
    public int modifyFireRate(int fireRate) {
        return Math.max(1, fireRate - 2);
    }
}
