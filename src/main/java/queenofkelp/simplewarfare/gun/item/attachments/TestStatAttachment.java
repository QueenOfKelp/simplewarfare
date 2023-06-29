package queenofkelp.simplewarfare.gun.item.attachments;

public class TestStatAttachment extends AbstractStatModifyingAttachmentItem{
    public TestStatAttachment(Settings settings) {
        super(settings);
    }

    @Override
    public int modifyFireRate(int fireRate) {
        return Math.max(1, fireRate - 2);
    }
}
