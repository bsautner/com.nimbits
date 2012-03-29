package com.nimbits.client.constants;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/27/12
 * Time: 2:46 PM
 */
public class UserMessages {
    public static final String RESPONSE_PERMISSION_DENIED = "Permission Denied";
    public static final String RESPONSE_PROTECTED_POINT = "Unable to process. You didn't provide an oauth token or secret, and the point you requested is not public";
    public static final String ERROR_POINT_NOT_FOUND = "Point not found";
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_RETRY = "There was an error saving this value. The system will try again.";
    public static final String COPYRIGHT = "Copyright (c) 2010 Tonic Solutions LLC.  Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an \\\"AS IS\\\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.";
    public static final String ERROR_BATCH_SERVICE_JDO = "Batch Service JDOException";
    public static final String MESSAGE_ADD_CATEGORY = "Add a new data point Category";
    public static final String MESSAGE_DATA_POINT = "Data Point";
    public static final String MESSAGE_EMAIL_SUBJECT = "Message from nimbits.com";
    public static final String MESSAGE_NEW_POINT = "New Data Point";
    public static final String MESSAGE_NEW_POINT_PROMPT = "Please enter the name of the new data point.";
    public static final String MESSAGE_NO_ACCOUNT = "No Google OwnerAccount";
    public static final String MESSAGE_SELECT_POINT = "Select a Data Point";
    public static final String MESSAGE_TWITTER_ADDED = "Nice! Your nimbits account is now connected to twitter. " +
            "You can configure data points to send alerts and updates to your twitter feed " +
            "on the property menu";
    public static final String MESSAGE_UPLOAD_SVG = "Upload a process diagram in .svg format";
    public static final String ERROR_QUOTA_EXCEEDED = "Daily Quota of " + Const.MAX_DAILY_QUOTA + " Exceeded. " +
            " and there are no billable units in your account. Please visit nimbits.com to enable billing";
    public static final String ERROR_NOT_IMPLEMENTED = "Not Implemented";
}
