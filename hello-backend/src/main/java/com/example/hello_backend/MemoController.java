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
    private List<Memo> memos=new ArrayList<>();
    private final String FILE_NAME="memos.txt";

    public MemoController(){
        //サーバー起動時にファイルから読み込む
        try{
            File file=new File(FILE_NAME);
            if(file.exists()){
                List<String> lines=Files.readAllLines(file.toPath());
                memos.clear();
                for(String line: lines){
                    String[] parts=line.split(",",2);//タイトル、内容
                    if(parts.length==2){
                        memos.add(new Memo(parts[0],parts[1]));
                    }
                }

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void saveToFile(){
        try{
            List<String> lines=memos.stream().map(m->m.getTitle()+","+m.getContent()).collect(Collectors.toList());
            
            Files.write(Paths.get(FILE_NAME),lines);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    //メモを追加
    @PostMapping
    public ResponseEntity<Memo> addMemo(@RequestBody Memo memo){
        memos.add(memo);
        saveToFile();//追加したら保存
        return ResponseEntity.ok(memo);

    }

    //メモを削除
    @DeleteMapping("/{index}")
    public ResponseEntity<?> deleteMemo(@PathVariable int index){
        if(index>=0 && index<memos.size()){
            Memo removed =memos.remove(index);
            saveToFile();//削除したら保存

            //削除したメモをJSONで返す
            return ResponseEntity.ok(removed);
        }else{
            //エラーメッセージもJSONにする
            Map<String,String> error=new HashMap<>();
            error.put("error","無効な番号です");
            return ResponseEntity.badRequest().body(error);
        }
    }

    //メモを編集
    @PutMapping("/{index}")
    public ResponseEntity<?> updateMemo(@PathVariable int index, @RequestBody Memo updatedMemo){
        if(index>=0 && index<memos.size()){
            Memo memo=memos.get(index);

            if(updatedMemo.getTitle() !=null){
                memo.setTitle(updatedMemo.getTitle());
            }
            if(updatedMemo.getContent() !=null){
                memo.setContent(updatedMemo.getContent());
            }
            
            saveToFile();//更新したら保存
            return ResponseEntity.ok(memo);
        }else{
            Map<String,String> error=new HashMap<>();
            error.put("error","無効な番号です");
            return ResponseEntity.badRequest().body(error);
        }
    }

    //メモ一覧を取得
    @GetMapping
    public List<Memo> getMemos(){
        return memos;
    }
}
