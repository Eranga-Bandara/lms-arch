package lk.ijse.dep9.service;

import lk.ijse.dep9.service.custom.BookService;
import lk.ijse.dep9.service.custom.impl.BookServiceImpl;
import lk.ijse.dep9.service.custom.impl.IssueServiceImpl;
import lk.ijse.dep9.service.custom.impl.MemberServiceImpl;
import lk.ijse.dep9.service.custom.impl.ReturnServiceImpl;

public class ServiceFactory {

    // making singleton
    private static ServiceFactory serviceFactory;

    public ServiceFactory() {
    }

    public static ServiceFactory getInstance() {
        return (serviceFactory == null)? (serviceFactory = new ServiceFactory()): serviceFactory;
    }

    public <T extends SuperService> T getService(ServiceTypes serviceType){
        SuperService service;
        switch (serviceType){
            case BOOK:
                service = new BookServiceImpl();
                break;
            case ISSUE:
                service = new IssueServiceImpl();
                break;
            case MEMBER:
                service = new MemberServiceImpl();
                break;
            case RETURN:
                service = new ReturnServiceImpl();
                break;
            default:
                service = null;
                break;
        }
        return (T) service;
    }
}
