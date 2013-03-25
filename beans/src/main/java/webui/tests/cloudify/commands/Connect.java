package webui.tests.cloudify.commands;

/**
 * User: guym
 * Date: 3/21/13
 * Time: 11:59 AM
 */
public class Connect extends CloudifyCommand<Connect>{

    public Connect user( String user ){
        return arg( "-user",user );
    }

    public Connect password( String password ){
        return arg( "-password", password );
    }

    public Connect restUrl( String restUrl ){
        return arg( restUrl );
    }

    public static class Details extends CloudifyCommand.Details<Connect>{
        private String user;
        private String password;
        private String restUrl;

        @Override
        public Connect populate( Connect connect ) {
            return connect._if( user ).arg( user )._if( password ).arg( password )._if( restUrl ).arg( restUrl );
        }

        public Details setUser( String user ) {
            this.user = user;
            return this;
        }

        public Details setPassword( String password ) {
            this.password = password;
            return this;
        }

        public Details setRestUrl( String restUrl ) {
            this.restUrl = restUrl;
            return this;
        }
    }
}
