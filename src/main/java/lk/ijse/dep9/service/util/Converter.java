package lk.ijse.dep9.service.util;

import lk.ijse.dep9.dto.*;
import lk.ijse.dep9.entity.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;

import java.lang.reflect.Type;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    private ModelMapper mapper;

    public Converter() {
        this.mapper = new ModelMapper();
        mapper.typeMap(LocalDate.class, Date.class).setConverter(modelContext -> Date.valueOf(modelContext.getSource()));
    }

    public BookDTO fromBook(Book bookEntity) {
//        return new BookDTO(bookEntity.getIsbn(),
//                bookEntity.getTitle(),
//                bookEntity.getAuthor(),
//                bookEntity.getCopies());
        return mapper.map(bookEntity, BookDTO.class);
    }

    public Book toBook(BookDTO bookDTO) {
//        return new Book(bookDTO.getIsbn(),
//                bookDTO.getTitle(),
//                bookDTO.getAuthor(),
//                bookDTO.getCopies());
        return mapper.map(bookDTO, Book.class);
    }

    public MemberDTO fromMember(Member memberEntity) {
        return mapper.map(memberEntity, MemberDTO.class);
    }

    public Member toMember(MemberDTO memberDTO) {
        return mapper.map(memberDTO, Member.class);
    }

    public IssueNote toIssueNote(IssueNoteDTO issueNoteDTO) {

        return mapper.map(issueNoteDTO, IssueNote.class);
    }

    public List<IssueItem> toIssueItemList(IssueNoteDTO issueNoteDTO) {

        Type typeToken = new TypeToken<List<IssueItem>>(){}.getType();
        mapper.typeMap(IssueNoteDTO.class, List.class).setConverter(mc -> {
            IssueNoteDTO source = mc.getSource();
            return source.getBooks().stream().map(isbn ->
                    new IssueItem(source.getId(), isbn)).collect(Collectors.toList());
        });
        return mapper.map(issueNoteDTO, typeToken);
    }

    public Return toReturn(ReturnItemDTO returnItemDTO){

        mapper.typeMap(ReturnItemDTO.class, Return.class).setConverter(mc ->
                new Return(null, mc.getSource().getIssueNoteId(), mc.getSource().getIsbn()));
        return mapper.map(returnItemDTO, Return.class);
    }

}
