-- ==============================
-- data.sql - 测试数据
-- ==============================

-- -------- dict 表 ----------
INSERT INTO dict(dict_id, dict_type_code, dict_type_name, dict_item_name, is_enabled, creater, updater)
VALUES
    (1, 'OS_TYPE', '操作系统', 'Windows 10', 1, 'system', 'system'),
    (2, 'OS_TYPE', '操作系统', 'Ubuntu 22.04', 1, 'system', 'system'),
    (3, 'MEMORY_SIZE', 'メモリ', '16GB', 1, 'system', 'system'),
    (4, 'SSD_SIZE', 'SSD容量', '512GB', 1, 'system', 'system'),
    (5, 'HDD_SIZE', 'HDD容量', '1TB', 1, 'system', 'system'),
    (6, 'USER_TYPE', 'ユーザタイプ', '管理员', 1, 'system', 'system'),
    (7, 'CONFIRM_STATUS', '本人確認', '已確認', 1, 'system', 'system');

-- -------- users 表 ----------
INSERT INTO users(user_id, dept_id, name, user_type_id, password, creater, updater)
VALUES
    ('U001', 'D001', '山田太郎', 6, 'password1', 'system', 'system'),
    ('U002', 'D002', '佐藤花子', 6, 'password2', 'system', 'system');

-- -------- device_info 表 ----------
INSERT INTO device_info(device_id, device_model, computer_name, login_username, project, dev_room, user_id, self_confirm_id, os_id, memory_id, ssd_id, hdd_id, creater, updater)
VALUES
    ('DEV001', 'Dell XPS 13', 'Taro-PC', 'taro', 'ProjectA', 'DevRoom1', 'U001', 7, 1, 3, 4, 5, 'system', 'system'),
    ('DEV002', 'Lenovo ThinkPad', 'Hanako-PC', 'hanako', 'ProjectB', 'DevRoom2', 'U002', 7, 2, 3, 4, 5, 'system', 'system');

-- -------- device_ip 表 ----------
INSERT INTO device_ip(ip_id, device_id, ip_address, creater, updater)
VALUES
    (1, 'DEV001', '192.168.1.101', 'system', 'system'),
    (2, 'DEV002', '192.168.1.102', 'system', 'system');

-- -------- device_permission 表 ----------
INSERT INTO device_permission(permission_id, device_id, domain_status_id, domain_group, smartit_status_id, usb_status_id, antivirus_status_id, creater, updater)
VALUES
    ('P001', 'DEV001', 7, 'DomainA', 7, 7, 7, 'system', 'system'),
    ('P002', 'DEV002', 7, 'DomainB', 7, 7, 7, 'system', 'system');

-- -------- monitor_info 表 ----------
INSERT INTO monitor_info(monitor_id, device_id, monitor_name, creater, updater)
VALUES
    (1, 'DEV001', 'Dell Monitor', 'system', 'system'),
    (2, 'DEV002', 'Lenovo Monitor', 'system', 'system');

-- -------- sampling_check 表 ----------
INSERT INTO sampling_check(sampling_id, user_id, device_id, installed_software, name, report_id, update_date, screen_saver_pwd, usb_interface, security_patch, antivirus_protection, boot_authentication, creater, updater)
VALUES
    ('S001', 'U001', 'DEV001', true, '山田太郎', 'R001', CURRENT_DATE, true, true, true, true, true, 'system', 'system'),
    ('S002', 'U002', 'DEV002', false, '佐藤花子', 'R002', CURRENT_DATE, false, true, false, true, false, 'system', 'system');
