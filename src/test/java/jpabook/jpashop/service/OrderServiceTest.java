package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember("회원1");

        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER,getOrder.getStatus());
        assertEquals(1,getOrder.getOrderItems().size());
        assertEquals(10000*orderCount,getOrder.getTotalPrice());
        assertEquals(8,book.getStockQuantity());
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember("회원1");
//        Member member  = Mockito.mock(Member.class);
        Item book = createBook("시골 JPA", 10000, 10);
        int orderCount = book.getStockQuantity()+1;
        //when
        try {
            orderService.order(member.getId(),book.getId(),orderCount);
        } catch (NotEnoughStockException e) {
            //주문수량 부족예외가 터지면 성공
            return;
        }
        //then
        fail("재고 수량 부족 예외가 발생해야 한다");
    }

//    @Test(expected = NotEnoughStockException.class)
    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember("회원1");
        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;
        //주문까지 셋팅
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        //주문을 취소하면 //삭제는 아니네? 상태만 cancel로
        orderService.cancelOrder(orderId);

        //then
        //취소한 order를 가져온뒤
        Order getOrder = orderRepository.findOne(orderId);

        //상태는 취소,수량은 원복 되있어야한다
        assertEquals(OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals(10,book.getStockQuantity());
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울","경기","123-123"));
        em.persist(member);
        return member;
    }
}