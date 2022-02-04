package com.vvishwanath.fbla.ui.messages.tasks;

import android.os.AsyncTask;

import com.vvishwanath.fbla.objects.Message;

@SuppressWarnings("ALL")
public class Task {

    public static class Insert extends AsyncTask<Message, Void, Message> {

        private final MessageDao messageDao;

        public Insert(MessageDao dao) {
            messageDao = dao;
        }

        @Override
        protected Message doInBackground(Message... messages) {
            messageDao.insertMessages(messages);
            return null;
        }
    }

    public static class Delete extends AsyncTask<Message, Void, Message> {

        private final MessageDao messageDao;

        public Delete(MessageDao dao) {
            messageDao = dao;
        }

        @Override
        protected Message doInBackground(Message... messages) {
            messageDao.delete(messages);
            return null;
        }
    }

    public static class Update extends AsyncTask<Message, Void, Message> {

        private final MessageDao messageDao;

        public Update(MessageDao dao) {
            messageDao = dao;
        }

        @Override
        protected Message doInBackground(Message... messages) {
            messageDao.updateMessages(messages);
            return null;
        }
    }
}
