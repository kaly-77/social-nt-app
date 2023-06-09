package com.kaly7dev.socialntapp.services;

import com.kaly7dev.socialntapp.coreapi.dtos.PostRequest;
import com.kaly7dev.socialntapp.coreapi.dtos.PostResponse;
import com.kaly7dev.socialntapp.coreapi.exceptions.PostNotFoundException;
import com.kaly7dev.socialntapp.coreapi.exceptions.SubsocialNtNotFoundException;
import com.kaly7dev.socialntapp.coreapi.exceptions.UserNotFoundException;
import com.kaly7dev.socialntapp.coreapi.mappers.PostMapper;
import com.kaly7dev.socialntapp.entities.Post;
import com.kaly7dev.socialntapp.entities.SubsocialNt;
import com.kaly7dev.socialntapp.entities.User;
import com.kaly7dev.socialntapp.repositories.PostRepo;
import com.kaly7dev.socialntapp.repositories.SubsocialNtRepo;
import com.kaly7dev.socialntapp.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepo postRepo;
    private final SubsocialNtRepo subsocialNtRepo;
    private final PostMapper postMapper;
    private final AuthService authService;
    private final UserRepo userRepo;
    @Override
    public void create(PostRequest postRequest) {
        SubsocialNt subsocialNt= subsocialNtRepo.findByName(postRequest.getSubsocialntName())
                .orElseThrow(()->new SubsocialNtNotFoundException(postRequest.getSubsocialntName()));
        postRepo.save(postMapper.map(postRequest, subsocialNt, authService.getCurrentUser()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepo.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepo.findById(id).orElseThrow(
                ()->new PostNotFoundException(id.toString()));
        return postMapper.mapToDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostListBySubsocialNt(Long subsocialntId) {
        SubsocialNt subsocialNt= subsocialNtRepo.findById(subsocialntId)
                .orElseThrow(()->new SubsocialNtNotFoundException(subsocialntId.toString()));
        List<Post> postList= postRepo.findAllBySubsocialNt(subsocialNt);
        return postList.stream().map(postMapper::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostListByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException(username));
        List<Post> postList= postRepo.findAllByUser(user);
        return postList.stream()
                .map(postMapper::mapToDto)
                .toList();
    }
}
