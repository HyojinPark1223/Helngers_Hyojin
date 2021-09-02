package com.hj.follow.controller;


import com.hj.follow.dto.FollowDto;
import com.hj.follow.service.FollowServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = { "*" })
@RestController
@RequestMapping("/")
public class MemberController {

    @Autowired
    private FollowServiceImpl followsvc;

    @PostMapping(value ="follow")
    public FollowDto follow(@RequestBody FollowDto followDto) throws Exception {
        int from_id = followDto.getFrom_id();
        int to_id = followDto.getTo_id();

        System.out.println("****잘 돌아가는지 확인용, from_id:" + from_id);
        System.out.println("****잘 돌아가는지 확인용, to_id:" + to_id);
        // 맞팔을 했는지 체크.(맞팔을 안했음 0이 나올 것)
        int check = followsvc.checkFollows(followDto);
        System.out.println("****잘 돌아가는지 확인용(check값):" + check);
        if(check>0){ // 상대가 나를 팔로우 하고 있다면,
            // follows = 1로 들어갈 것.
            followsvc.willFollows(followDto);

            // 상대의 기존 follows 상태도 1로 변경해줘야함.
            followsvc.updateFollows(followDto);

            followDto.setFollows(true);

        }else{ // 상대가 나를 팔로우 한 상태가 아닐 때,
            followsvc.onewayFollows(followDto);
        }

        System.out.println("****return 되는 dto 확인확인!!!:"+followDto.toString());
        return followDto;
    }

    // 내가(유저) 팔로우를 취소.
    @PostMapping(value = "unfollow")
    public FollowDto unfollow(@RequestBody FollowDto followDto) throws Exception{
        int from_id = followDto.getFrom_id();
        int to_id = followDto.getTo_id();

        System.out.println("****잘 돌아가는지 확인용, from_id:" + from_id);
        System.out.println("****잘 돌아가는지 확인용, to_id:" + to_id);

        int check = followsvc.checkFollows(followDto);

        System.out.println("****잘 돌아가는지 확인용, check:" + check);

        // 만약 서로 맞팔 상태라면 check = 1(true)이 들어가 있을 것.
        if(check>0){ // 맞팔인데 팔로우 끊으면,
               // 나는 삭제가 되고, 상대는 맞팔 처리가 되어있을 것이므로 수정해준다.
            followsvc.deleteFollows(followDto);
            System.out.println("둘이 맞팔상태였음! delete로직 돌아가는 중...");
        }
        followsvc.unfollow(followDto);

        System.out.println("****return 되는 dto 확인확인!!!:"+followDto.toString());
        return followDto;
    }

    // 팔로잉 리스트
    @PostMapping(value = "following/{from_id}")
    public List<FollowDto> following(@PathVariable int from_id) throws Exception{
        System.out.println("****from_id:"+from_id);

        List<FollowDto> following = new ArrayList<>();

        following = followsvc.followingList(from_id);

        return following;
    }

    // 팔로워 리스트
    @PostMapping(value = "follower/{to_id}")
    public List<FollowDto> follower(@PathVariable int to_id) throws Exception{
        System.out.println("****to_id:"+to_id);
        List<FollowDto> follower = new ArrayList<>();
        follower = followsvc.followerList(to_id);

        return follower;
    }
}