package lk.ijse.dep9.service;

import lk.ijse.dep9.service.custom.BookService;
import lk.ijse.dep9.service.custom.IssueService;
import lk.ijse.dep9.service.custom.MemberService;
import lk.ijse.dep9.service.custom.ReturnService;
import lk.ijse.dep9.service.custom.impl.BookServiceImpl;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceFactoryTest {

    @RepeatedTest(2)
    void getInstance() {
        ServiceFactory instance = ServiceFactory.getInstance();
        ServiceFactory instance2 = ServiceFactory.getInstance();
        ServiceFactory instance3 = ServiceFactory.getInstance();

        assertEquals(instance, instance2);
        assertEquals(instance, instance3);
        assertEquals(instance2, instance3);
    }

    @Test
    void getService() {
        SuperService bookService = ServiceFactory.getInstance().getService(ServiceTypes.BOOK);
        SuperService issueService = ServiceFactory.getInstance().getService(ServiceTypes.ISSUE);
        SuperService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
        SuperService returnService = ServiceFactory.getInstance().getService(ServiceTypes.RETURN);

        assertInstanceOf(BookServiceImpl.class, bookService);
        assertInstanceOf(IssueService.class, issueService);
        assertInstanceOf(MemberService.class, memberService);
        assertInstanceOf(ReturnService.class, returnService);

    }
}