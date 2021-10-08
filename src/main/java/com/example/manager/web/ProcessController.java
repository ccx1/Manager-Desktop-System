package com.example.manager.web;

import com.example.manager.dto.CommandDto;
import com.example.manager.dto.ProcessDto;
import com.example.manager.result.ResultEntity;
import com.example.manager.utils.ProcessUtils;
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
    public ResultEntity<List<ProcessDto>> listAllProcess(@RequestParam(value = "key", required = false) String key) {
        return ResultEntity.ok(ProcessUtils.listAllProcess(key));

    }

    @GetMapping("/kill")
    public ResultEntity<Integer> kill(@RequestParam("pid") int pid) {
        return ResultEntity.ok(ProcessUtils.stopProcess(pid));

    }

}
