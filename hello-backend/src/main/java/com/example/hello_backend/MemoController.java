package com.example.hello_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@CrossOrigin(origins = "*")//Live
@RestController
@RequestMapping("/memos")
public class MemoController {
    
    //メモを保存するリスト（今回はDBなし）
    
    private final String FILE_NAME="memos.txt";

    private final MemoRepository memoRepository;

    public MemoController(MemoRepository memoRepository){
        this.memoRepository=memoRepository;
    }



    //メモを追加
    @PostMapping
    public ResponseEntity<Memo> addMemo(@RequestBody Memo memo){
        Memo savedMemo=memoRepository.save(memo);
        return ResponseEntity.ok(savedMemo);

    }

    //メモを削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable int id){
            //削除したら保存
            if(!memoRepository.existsById(id)){//指定された荷物が存在しない
                return ResponseEntity.notFound().build();//404を返す
            }
            memoRepository.deleteById(id);//DBから削除
            //削除成功
            return ResponseEntity.noContent().build();//ボディは返さない
            //204を返す
    }

    //メモを編集
    @PutMapping("/{id}")
    public ResponseEntity<Memo> updateMemo(@PathVariable int id, @RequestBody Memo updatedMemo){
            Optional<Memo>optionalMemo=memoRepository.findById(id);
            if(optionalMemo.isEmpty()) return ResponseEntity.notFound().build();//存在しない場合

            Memo memo=optionalMemo.get();
            
            //変更をしていないところをヌルにしないようにする
            if(updatedMemo.getTitle() !=null){
                memo.setTitle(updatedMemo.getTitle());
            }
            if(updatedMemo.getContent() !=null){
                memo.setContent(updatedMemo.getContent());
            }
            
            memoRepository.save(memo);//ここでDBに反映
            //更新したら保存
            return ResponseEntity.ok(memo);//更新後の荷物返す
    }

    //メモ一覧を取得
    @GetMapping
    public List<Memo> getMemos(){
        return memoRepository.findAll();//DBから取得
    }
}
