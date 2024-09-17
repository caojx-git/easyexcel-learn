package caojx.learn.easyexcellearn.exception;

/**
 * 类注释，描述 //TODO
 *
 * @author caojx
 * @since 2024/1/31 11:45
 */
public class ServiceException extends RuntimeException{

    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}