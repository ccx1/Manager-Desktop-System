package com.example.file.web;

import com.example.file.dto.CommandDto;
import com.example.file.dto.ProcessDto;
import com.example.file.result.ResultEntity;
import com.example.file.utils.ProcessUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：chicunxiang
 * @date ：Created in 2021/10/3 10:14
 * @description：
 * @version: 1.0
 */
@RequestMapping("/process")
@RestController
public class ProcessController {


    @PostMapping("/cmd")
    public ResultEntity<String> cmd(@RequestBody CommandDto cmd) {
        return ResultEntity.ok(ProcessUtils.runCommand(cmd.getCmd()));

    }


    @GetMapping("/list")
    public ResultEntity<List<ProcessDto>> listAllProcess() {
        return ResultEntity.ok(ProcessUtils.listAllProcess());

    }

    @GetMapping("/kill")
    public ResultEntity<Integer> kill(@RequestParam int pid) {
        return ResultEntity.ok(ProcessUtils.stopProcess(pid));

    }

}
