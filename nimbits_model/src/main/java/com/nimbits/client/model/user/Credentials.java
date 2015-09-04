package com.nimbits.client.model.user;

public class Credentials {

        private final String login;
        private final String password;

        public Credentials(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }
    }