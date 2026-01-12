-- ============================================
-- Device Management System Database Schema
-- PostgreSQL Version
-- ============================================

-- Drop existing tables if they exist
DROP TABLE IF EXISTS SECURITY_CHECKS CASCADE;
DROP TABLE IF EXISTS DEVICES_USAGE_PERMISSIONS CASCADE;
DROP TABLE IF EXISTS DEVICES CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;

-- ============================================
-- Table 1: USERS - 用户表
-- ============================================
CREATE TABLE USERS (
    USER_ID VARCHAR(20) PRIMARY KEY,
    USER_NAME VARCHAR(100) NOT NULL,
    DEPARTMENT_CODE VARCHAR(20) NOT NULL,
    USER_LEVEL VARCHAR(20) NOT NULL,
    PASSWORD_HASH VARCHAR(200) NOT NULL,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Table 2: DEVICES - 设备表
-- ============================================
CREATE TABLE DEVICES (
    DEVICE_ID BIGSERIAL PRIMARY KEY,
    USER_ID VARCHAR(20) NOT NULL,
    COMPUTER_ID VARCHAR(100) NOT NULL,
    MONITER_ID VARCHAR(100) NOT NULL,
    COMPUTER_MODEL VARCHAR(100),
    COMPUTER_NAME VARCHAR(100),
    IP VARCHAR(100),
    OS VARCHAR(10),
    MEMORY_SIZE INTEGER,
    SSD_SIZE INTEGER,
    HDD_SIZE INTEGER,
    LOGIN_USER VARCHAR(20),
    PROJECT_NAME VARCHAR(100),
    DEVELOPMENT_ROOM VARCHAR(100) NOT NULL,
    COMMET VARCHAR(200),
    IS_ACTIVE VARCHAR(1) DEFAULT 'Y',
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CREATED_USER VARCHAR(20),
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_USER VARCHAR(20),
    
    -- Foreign Key
    CONSTRAINT fk_devices_user FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

-- ============================================
-- Table 3: DEVICES_USAGE_PERMISSIONS - 设备使用权限表
-- ============================================
CREATE TABLE DEVICES_USAGE_PERMISSIONS (
    ID BIGSERIAL PRIMARY KEY,
    DEVICE_ID BIGINT NOT NULL,
    USER_ID VARCHAR(20) NOT NULL,
    SMARTIT_STATUS VARCHAR(20) NOT NULL,
    NO_SMARTIT_REASON VARCHAR(200),
    USB_STATUS VARCHAR(1) NOT NULL,
    USB_OPEN_REASON VARCHAR(200),
    USB_OPEN_ENDDATE TIMESTAMP,
    CONNECTION_STATUS VARCHAR(20) NOT NULL,
    NO_SYMENTEC_REASON VARCHAR(200),
    DOMAIN_NAME VARCHAR(100),
    DOMAIN_GROUP VARCHAR(100),
    NO_DOMAIN_REASON VARCHAR(200),
    COMMET VARCHAR(200),
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CREATED_USER VARCHAR(20),
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_USER VARCHAR(20),
    
    -- Foreign Keys
    CONSTRAINT fk_permission_device FOREIGN KEY (DEVICE_ID) REFERENCES DEVICES(DEVICE_ID) ON DELETE CASCADE,
    CONSTRAINT fk_permission_user FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE,
    
    -- Unique Constraint
    CONSTRAINT uk_device_user_permission UNIQUE (DEVICE_ID, USER_ID)
);

-- ============================================
-- Table 4: SECURITY_CHECKS - 安全检查表
-- ============================================
CREATE TABLE SECURITY_CHECKS (
    CHECK_ID BIGSERIAL PRIMARY KEY,
    DEVICE_ID BIGINT NOT NULL,
    USER_ID VARCHAR(20) NOT NULL,
    BOOT_AUTH VARCHAR(1) NOT NULL,
    PASSWORD_SCREEN VARCHAR(1) NOT NULL,
    INSTALLED_SOFTWARE VARCHAR(1) NOT NULL,
    SECURITY_PATCH VARCHAR(1) NOT NULL,
    VIRUS_PROTECTION VARCHAR(1) NOT NULL,
    USB_PORT VARCHAR(1) NOT NULL,
    HANDLING_MEASURES VARCHAR(200),
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CREATED_USER VARCHAR(20),
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_USER VARCHAR(20),
    
    -- Foreign Keys
    CONSTRAINT fk_check_device FOREIGN KEY (DEVICE_ID) REFERENCES DEVICES(DEVICE_ID) ON DELETE CASCADE,
    CONSTRAINT fk_check_user FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

-- ============================================
-- Create Indexes - 创建索引
-- ============================================

-- USERS table indexes
CREATE INDEX idx_user_name ON USERS(USER_NAME);
CREATE INDEX idx_department_code ON USERS(DEPARTMENT_CODE);

-- DEVICES table indexes
CREATE INDEX idx_device_user_id ON DEVICES(USER_ID);
CREATE INDEX idx_device_computer_id ON DEVICES(COMPUTER_ID);
CREATE INDEX idx_device_development_room ON DEVICES(DEVELOPMENT_ROOM);
CREATE INDEX idx_device_is_active ON DEVICES(IS_ACTIVE);
CREATE INDEX idx_device_created_date ON DEVICES(CREATED_DATE);

-- DEVICES_USAGE_PERMISSIONS table indexes
CREATE INDEX idx_permission_device_id ON DEVICES_USAGE_PERMISSIONS(DEVICE_ID);
CREATE INDEX idx_permission_user_id ON DEVICES_USAGE_PERMISSIONS(USER_ID);
CREATE INDEX idx_permission_created_date ON DEVICES_USAGE_PERMISSIONS(CREATED_DATE);

-- SECURITY_CHECKS table indexes
CREATE INDEX idx_check_device_id ON SECURITY_CHECKS(DEVICE_ID);
CREATE INDEX idx_check_user_id ON SECURITY_CHECKS(USER_ID);
CREATE INDEX idx_check_created_date ON SECURITY_CHECKS(CREATED_DATE);

-- ============================================
-- Comments - 表和列的说明文档
-- ============================================
COMMENT ON TABLE USERS IS '用户表 - 存储系统用户信息';
COMMENT ON COLUMN USERS.USER_ID IS '用户ID，主键';
COMMENT ON COLUMN USERS.USER_NAME IS '用户名';
COMMENT ON COLUMN USERS.DEPARTMENT_CODE IS '部门代码';
COMMENT ON COLUMN USERS.USER_LEVEL IS '用户级别 (ADMIN/USER)';
COMMENT ON COLUMN USERS.PASSWORD_HASH IS 'BCrypt加密的密码哈希';

COMMENT ON TABLE DEVICES IS '设备表 - 存储计算机设备信息';
COMMENT ON COLUMN DEVICES.DEVICE_ID IS '设备ID，自增主键';
COMMENT ON COLUMN DEVICES.USER_ID IS '所属用户ID，外键关联USERS表';
COMMENT ON COLUMN DEVICES.COMPUTER_ID IS '计算机编号';
COMMENT ON COLUMN DEVICES.MONITER_ID IS '显示器编号';
COMMENT ON COLUMN DEVICES.DEVELOPMENT_ROOM IS '开发室/办公室名称';
COMMENT ON COLUMN DEVICES.IS_ACTIVE IS '是否激活 (Y/N)，默认Y';

COMMENT ON TABLE DEVICES_USAGE_PERMISSIONS IS '设备使用权限表 - 管理用户对设备的访问权限';
COMMENT ON COLUMN DEVICES_USAGE_PERMISSIONS.DEVICE_ID IS '设备ID，外键关联DEVICES表';
COMMENT ON COLUMN DEVICES_USAGE_PERMISSIONS.USER_ID IS '用户ID，外键关联USERS表';
COMMENT ON COLUMN DEVICES_USAGE_PERMISSIONS.SMARTIT_STATUS IS 'SMARTIT状态: 0=本地, 1=远程, 2=未安装';
COMMENT ON COLUMN DEVICES_USAGE_PERMISSIONS.USB_STATUS IS 'USB状态: 0=关闭, 1=数据, 2=3G调制解调器';
COMMENT ON COLUMN DEVICES_USAGE_PERMISSIONS.CONNECTION_STATUS IS '连接状态: 0=自动, 1=手动';

COMMENT ON TABLE SECURITY_CHECKS IS '安全检查表 - 记录设备安全检查情况';
COMMENT ON COLUMN SECURITY_CHECKS.DEVICE_ID IS '设备ID，外键关联DEVICES表';
COMMENT ON COLUMN SECURITY_CHECKS.USER_ID IS '用户ID，外键关联USERS表';
COMMENT ON COLUMN SECURITY_CHECKS.BOOT_AUTH IS '启动认证: Y=有, N=无';
COMMENT ON COLUMN SECURITY_CHECKS.PASSWORD_SCREEN IS '密码屏保: Y=有, N=无';
COMMENT ON COLUMN SECURITY_CHECKS.INSTALLED_SOFTWARE IS '安装软件: Y=有, N=无';
COMMENT ON COLUMN SECURITY_CHECKS.SECURITY_PATCH IS '安全补丁: Y=有, N=无';
COMMENT ON COLUMN SECURITY_CHECKS.VIRUS_PROTECTION IS '病毒防护: Y=有, N=无';
COMMENT ON COLUMN SECURITY_CHECKS.USB_PORT IS 'USB端口: Y=有, N=无';

-- ============================================
-- Sample Data - 初始化数据（可选）
-- ============================================

-- 插入默认用户
INSERT INTO USERS (USER_ID, USER_NAME, DEPARTMENT_CODE, USER_LEVEL, PASSWORD_HASH)
VALUES 
    ('admin', '管理员', 'ADMIN', 'ADMIN', '$2a$10$NxbQzHvvXl7sVmKx3JNLYuVJa/GCNnb6P9DpAGnYSBzWvYZhJ0jau'),
    ('EMP001', '员工001', 'DEPT001', 'USER', '$2a$10$NxbQzHvvXl7sVmKx3JNLYuVJa/GCNnb6P9DpAGnYSBzWvYZhJ0jau'),
    ('EMP002', '员工002', 'DEPT001', 'USER', '$2a$10$NxbQzHvvXl7sVmKx3JNLYuVJa/GCNnb6P9DpAGnYSBzWvYZhJ0jau');

-- ============================================
-- View: 用户设备信息视图
-- ============================================
CREATE VIEW v_user_devices AS
SELECT 
    u.USER_ID,
    u.USER_NAME,
    u.DEPARTMENT_CODE,
    d.DEVICE_ID,
    d.COMPUTER_ID,
    d.COMPUTER_NAME,
    d.IP,
    d.DEVELOPMENT_ROOM,
    d.IS_ACTIVE,
    d.CREATED_DATE
FROM USERS u
LEFT JOIN DEVICES d ON u.USER_ID = d.USER_ID
ORDER BY u.USER_ID, d.DEVICE_ID;

-- ============================================
-- Index Creation Summary
-- ============================================
-- USERS table indexes:
--   - idx_user_name: 用户名查询
--   - idx_department_code: 部门代码查询
--
-- DEVICES table indexes:
--   - idx_device_user_id: 用户关联查询
--   - idx_device_computer_id: 计算机编号查询
--   - idx_device_development_room: 开发室查询
--   - idx_device_is_active: 激活状态查询
--   - idx_device_created_date: 创建时间排序
--
-- DEVICES_USAGE_PERMISSIONS table indexes:
--   - idx_permission_device_id: 设备权限查询
--   - idx_permission_user_id: 用户权限查询
--   - idx_permission_created_date: 创建时间排序
--
-- SECURITY_CHECKS table indexes:
--   - idx_check_device_id: 设备检查查询
--   - idx_check_user_id: 用户检查查询
--   - idx_check_created_date: 创建时间排序

-- ============================================
-- End of Schema Creation Script
-- ============================================
