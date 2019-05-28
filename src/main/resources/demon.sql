-- dpm_imports
INSERT INTO `dpm_imports`(`ID`, `CREATED_BY`, `CREATED_AT`, `UPDATED_BY`, `UPDATED_AT`, `imports`) VALUES (1, 'sys', CURRENT_TIMESTAMP , '', CURRENT_TIMESTAMP, 'org.apache.commons.lang3.StringUtils');
INSERT INTO `dpm_imports`(`ID`, `CREATED_BY`, `CREATED_AT`, `UPDATED_BY`, `UPDATED_AT`, `imports`) VALUES (2, 'sys', CURRENT_TIMESTAMP, '', CURRENT_TIMESTAMP, 'org.apache.commons.lang3.time.DateUtils');

-- dpm_functions
INSERT INTO `dpm_functions`(`ID`, `CREATED_BY`, `CREATED_AT`, `UPDATED_BY`, `UPDATED_AT`, `func_code`, `func_desc`, `func_name`, `func_status`) VALUES (1, 'sys', CURRENT_TIMESTAMP, 'sys', CURRENT_TIMESTAMP, '(def x,def y){\n        x+y\n    }', '相加', 'add', 'ON');

-- dpm_indicators
INSERT INTO `dpm_indicators`(`ID`, `CREATED_BY`, `CREATED_AT`, `UPDATED_BY`, `UPDATED_AT`, `ind_grade`, `ind_desc`, `ind_key`, `source_code`, `ind_status`, `type`, `update_reason`, `ind_version`) VALUES (1, 'sys', CURRENT_TIMESTAMP, 'sys', CURRENT_TIMESTAMP, 1, '测试指标1', 'my_add', 'add(data.aa,data.bb)', 'ON', 'TEST', '示例指标', 1);