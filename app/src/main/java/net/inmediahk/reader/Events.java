package net.inmediahk.reader;

public class Events {

    public static class FeedAdapterUpdatedEvent{

        int id;
        public FeedAdapterUpdatedEvent(int id) {
            this.id = id;
        }
    }
}
