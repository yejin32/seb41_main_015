package com.book.village.server.domain.community;

import com.book.village.server.domain.community.controller.CommunityController;
import com.book.village.server.domain.community.dto.CommunityDto;
import com.book.village.server.domain.community.entity.Community;
import com.book.village.server.domain.community.mapper.CommunityMapper;
import com.book.village.server.domain.community.service.CommunityService;
import com.book.village.server.domain.member.entity.Member;
import com.book.village.server.global.utils.GenerateMockToken;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.book.village.server.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.book.village.server.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommunityController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class CommunityControllerRestDocsTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommunityService communityService;

    @MockBean
    private CommunityMapper mapper;

    @Autowired
    private Gson gson;

    private static final String url = "/v1/communities";

    @Test
    @DisplayName("커뮤니티 등록")
    @WithMockUser
    public void postCommunityTest() throws Exception {
        long communityId=1L;
        CommunityDto.Post post = new CommunityDto.Post(
                "free",
                "title1",
                "content1",
                "displayName1"
        );

        LocalDateTime createdAt=LocalDateTime.now();
        LocalDateTime modifiedAt=createdAt;
        String content = gson.toJson(post);

        CommunityDto.Response response = new CommunityDto.Response(
                communityId,
                "free",
                "title1",
                "content1",
                "displayName1",
                createdAt,
                modifiedAt
        );

        given(mapper.postCommunityDtoToCommunity(Mockito.any(CommunityDto.Post.class))).willReturn(new Community());
        given(communityService.createCommunity(Mockito.any(Community.class),Mockito.anyString())).willReturn(new Community());

        given(mapper.communityToCommunityResponseDto(Mockito.any(Community.class))).willReturn(response);


        ResultActions actions =
                mockMvc.perform(
                        post(url)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .headers(GenerateMockToken.getMockHeaderToken())
                );
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value(post.getType()))
                .andExpect(jsonPath("$.data.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.content").value(post.getContent()))
                .andExpect(jsonPath("$.data.displayName").value(post.getDisplayName()))
                .andDo(document("post-community",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer Token")
                        ),
                        // request body
                        requestFields(
                                List.of(
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("카테고리[notice, free, study, recommend]"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("작성자").ignored()
                                )
                        ),
                        // response body
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.communityId").type(JsonFieldType.NUMBER).description("커뮤니티 식별자"),
                                        fieldWithPath("data.type").type(JsonFieldType.STRING).description("카테고리[notice, free, study, recommend]"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.displayName").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일자"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 일자")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("커뮤니티 수정")
    @WithMockUser
    public void patchCommunityTest() throws Exception {
        long communityId=1L;
        CommunityDto.Patch patch = new CommunityDto.Patch(
                communityId,
                "free",
                "title1",
                "content1",
                "displayName1"
        );

        LocalDateTime createdAt=LocalDateTime.now();
        LocalDateTime modifiedAt=createdAt;
        String content = gson.toJson(patch);

        CommunityDto.Response response = new CommunityDto.Response(
                communityId,
                "free",
                "title1",
                "content1",
                "displayName1",
                createdAt,
                modifiedAt
        );

        given(mapper.patchCommunityDtoToCommunity(Mockito.any(CommunityDto.Patch.class))).willReturn(new Community());
        given(communityService.updateCommunity(Mockito.any(Community.class),Mockito.anyString())).willReturn(new Community());

        given(mapper.communityToCommunityResponseDto(Mockito.any(Community.class))).willReturn(response);


        ResultActions actions =
                mockMvc.perform(
                        patch(url+"/{community-id}",communityId)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .headers(GenerateMockToken.getMockHeaderToken())
                );
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.communityId").value(patch.getCommunityId()))
                .andExpect(jsonPath("$.data.type").value(patch.getType()))
                .andExpect(jsonPath("$.data.title").value(patch.getTitle()))
                .andExpect(jsonPath("$.data.content").value(patch.getContent()))
                .andExpect(jsonPath("$.data.displayName").value(patch.getDisplayName()))
                .andDo(document("patch-community",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer Token")
                        ),
                        pathParameters(
                                parameterWithName("community-id").description("커뮤니티 식별자")
                        ),
                        // request body
                        requestFields(
                                List.of(
                                        fieldWithPath("communityId").type(JsonFieldType.STRING).description("커뮤니티 식별자").ignored(),
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("카테고리[notice, free, study, recommend]").optional(),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용").optional(),
                                        fieldWithPath("displayName").type(JsonFieldType.STRING).description("작성자").optional()
                                )
                        ),
                        // response body
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.communityId").type(JsonFieldType.NUMBER).description("커뮤니티 식별자"),
                                        fieldWithPath("data.type").type(JsonFieldType.STRING).description("카테고리[notice, free, study, recommend]"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.displayName").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일자"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 일자")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("커뮤니티 조회")
    @WithMockUser
    public void getCommunityTest() throws Exception {
        long communityId=1L;

        LocalDateTime createdAt=LocalDateTime.now();
        LocalDateTime modifiedAt=createdAt;

        CommunityDto.Response response = new CommunityDto.Response(
                communityId,
                "free",
                "title1",
                "content1",
                "displayName1",
                createdAt,
                modifiedAt
        );

        given(communityService.findCommunity(Mockito.anyLong())).willReturn(new Community());
        given(mapper.communityToCommunityResponseDto(Mockito.any(Community.class))).willReturn(response);


        ResultActions actions =
                mockMvc.perform(
                        get(url+"/{community-id}",communityId)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                );
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.communityId").value(response.getCommunityId()))
                .andExpect(jsonPath("$.data.type").value(response.getType()))
                .andExpect(jsonPath("$.data.title").value(response.getTitle()))
                .andExpect(jsonPath("$.data.content").value(response.getContent()))
                .andExpect(jsonPath("$.data.displayName").value(response.getDisplayName()))
                .andDo(document("get-community",
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("community-id").description("커뮤니티 식별자")
                        ),
                        // response body
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.communityId").type(JsonFieldType.NUMBER).description("커뮤니티 식별자"),
                                        fieldWithPath("data.type").type(JsonFieldType.STRING).description("카테고리[notice, free, study, recommend]"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.displayName").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일자"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 일자")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("모든 커뮤니티 조회")
    @WithMockUser
    public void getCommunitiesTest() throws Exception {

        LocalDateTime createdAt1=LocalDateTime.now();
        LocalDateTime createdAt2=createdAt1.minusDays(1L);

        CommunityDto.Response response1 = new CommunityDto.Response(
                1L,
                "free",
                "title1",
                "content1",
                "displayName1",
                createdAt1,
                createdAt1
        );
        CommunityDto.Response response2 = new CommunityDto.Response(
                2L,
                "free",
                "title2",
                "content2",
                "displayName2",
                createdAt2,
                createdAt2
        );

        List<CommunityDto.Response> responseList = new ArrayList<>();
        responseList.add(response1);
        responseList.add(response2);

        List<Community> list = new ArrayList<>();
        list.add(new Community(1L,
                "free",
                "title1",
                "content1",
                "displayName1",
                new Member()));
        list.add(new Community(
                2L,
                "free",
                "title2",
                "content2",
                "displayName2",
                new Member()));

        given(communityService.findCommunities(Mockito.any(Pageable.class))).willReturn(new PageImpl<>(
                list,
                PageRequest.of(0,10,
                        Sort.by("createdAt").descending()),2));
        given(mapper.communitiesToCommunityResponseDtos(Mockito.anyList())).willReturn(responseList);


        ResultActions actions =
                mockMvc.perform(
                        get(url)
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "questionId,desc")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                );
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(document("get-communities",
                        getResponsePreProcessor(),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈"),
                                parameterWithName("sort").description("정렬 기준[createdAt,desc]"),
                                parameterWithName("_csrf").description("csrf")
                        ),
                        // response body
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터"),
                                        fieldWithPath("data.[].communityId").type(JsonFieldType.NUMBER).description("커뮤니티 식별자"),
                                        fieldWithPath("data.[].type").type(JsonFieldType.STRING).description("카테고리[notice, free, study, recommend]"),
                                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.[].content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.[].displayName").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("data.[].createdAt").type(JsonFieldType.STRING).description("생성 일자"),
                                        fieldWithPath("data.[].modifiedAt").type(JsonFieldType.STRING).description("수정 일자"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.currentPage").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageInfo.totalPage").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("총 데이터 수"),
                                        fieldWithPath("pageInfo.pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                        fieldWithPath("pageInfo.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("pageInfo.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("pageInfo.currentElements").type(JsonFieldType.NUMBER).description("현재 페이지 데이터 수")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("나의 모든 커뮤니티 조회")
    @WithMockUser
    public void getMyCommunitiesTest() throws Exception {

        LocalDateTime createdAt1=LocalDateTime.now();
        LocalDateTime createdAt2=createdAt1.minusDays(1L);

        CommunityDto.Response response1 = new CommunityDto.Response(
                1L,
                "free",
                "title1",
                "content1",
                "displayName1",
                createdAt1,
                createdAt1
        );
        CommunityDto.Response response2 = new CommunityDto.Response(
                2L,
                "free",
                "title2",
                "content2",
                "displayName2",
                createdAt2,
                createdAt2
        );

        List<CommunityDto.Response> responseList = new ArrayList<>();
        responseList.add(response1);
        responseList.add(response2);

        List<Community> list = new ArrayList<>();
        list.add(new Community(1L,
                "free",
                "title1",
                "content1",
                "displayName1",
                new Member()));
        list.add(new Community(
                2L,
                "free",
                "title2",
                "content2",
                "displayName2",
                new Member()));

        given(communityService.findMyCommunities(Mockito.anyString(), Mockito.any(Pageable.class))).willReturn(new PageImpl<>(
                list,
                PageRequest.of(0,10,
                        Sort.by("createdAt").descending()),2));
        given(mapper.communitiesToCommunityResponseDtos(Mockito.anyList())).willReturn(responseList);


        ResultActions actions =
                mockMvc.perform(
                        get(url+"/mine")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "questionId,desc")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(GenerateMockToken.getMockHeaderToken())
                );
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(document("get-communities-mine",
                        getResponsePreProcessor(),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer Token")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈"),
                                parameterWithName("sort").description("정렬 기준[createdAt,desc]"),
                                parameterWithName("_csrf").description("csrf")
                        ),
                        // response body
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터"),
                                        fieldWithPath("data.[].communityId").type(JsonFieldType.NUMBER).description("커뮤니티 식별자"),
                                        fieldWithPath("data.[].type").type(JsonFieldType.STRING).description("카테고리[notice, free, study, recommend]"),
                                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.[].content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.[].displayName").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("data.[].createdAt").type(JsonFieldType.STRING).description("생성 일자"),
                                        fieldWithPath("data.[].modifiedAt").type(JsonFieldType.STRING).description("수정 일자"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.currentPage").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageInfo.totalPage").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("총 데이터 수"),
                                        fieldWithPath("pageInfo.pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                        fieldWithPath("pageInfo.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("pageInfo.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("pageInfo.currentElements").type(JsonFieldType.NUMBER).description("현재 페이지 데이터 수")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("커뮤니티 삭제")
    @WithMockUser
    public void deleteCommunityTest() throws Exception {
        long communityId=1L;

        LocalDateTime createdAt=LocalDateTime.now();
        LocalDateTime modifiedAt=createdAt;

        CommunityDto.Response response = new CommunityDto.Response(
                communityId,
                "free",
                "title1",
                "content1",
                "displayName1",
                createdAt,
                modifiedAt
        );

        doNothing().when(communityService).deleteCommunity(Mockito.anyLong(), Mockito.anyString());

        ResultActions actions =
                mockMvc.perform(
                        delete(url+"/{community-id}",communityId)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(GenerateMockToken.getMockHeaderToken())
                );
        actions
                .andExpect(status().isNoContent())
                .andDo(document("delete-community",
                        getResponsePreProcessor(),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer Token")
                        ),
                        pathParameters(
                                parameterWithName("community-id").description("커뮤니티 식별자")
                        )
                ));
    }
}
