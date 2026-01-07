<<<<<<< HEAD

-- ----------------------------
-- Sequence structure for dict_dict_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."dict_dict_id_seq";
CREATE SEQUENCE "public"."dict_dict_id_seq" 
INCREMENT 1
MINVALUE  1
=======
-- ============================================
-- Device Management System Database Schema
-- PostgreSQL Version
-- ============================================

-- Drop existing objects if they exist
DROP TABLE IF EXISTS sampling_check CASCADE;
DROP TABLE IF EXISTS monitor_info CASCADE;
DROP TABLE IF EXISTS device_permission CASCADE;
DROP TABLE IF EXISTS device_ip CASCADE;
DROP TABLE IF EXISTS device_info CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS dict CASCADE;
DROP SEQUENCE IF EXISTS dict_dict_id_seq;
DROP FUNCTION IF EXISTS update_modified_column();

-- ============================================
-- Sequence: dict_dict_id_seq
-- ============================================
CREATE SEQUENCE dict_dict_id_seq
INCREMENT 1
MINVALUE 1
>>>>>>> 0fcbd7ec8d221abe7008f62c49eb74283fbdb4d4
MAXVALUE 9223372036854775807
START 1
CACHE 1;

<<<<<<< HEAD
-- ----------------------------
-- Table structure for device_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."device_info";
CREATE TABLE "public"."device_info" (
  "device_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "device_model" varchar(100) COLLATE "pg_catalog"."default",
  "computer_name" varchar(100) COLLATE "pg_catalog"."default",
  "login_username" varchar(100) COLLATE "pg_catalog"."default",
  "project" varchar(100) COLLATE "pg_catalog"."default",
  "dev_room" varchar(100) COLLATE "pg_catalog"."default",
  "job_number" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" text COLLATE "pg_catalog"."default",
  "self_confirm_id" int8,
  "os_id" int8,
  "memory_id" int8,
  "ssd_id" int8,
  "hdd_id" int8,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "creater" varchar(100) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(100) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."device_info"."device_id" IS '機器番号（プライマリキー）';
COMMENT ON COLUMN "public"."device_info"."device_model" IS 'ホストモデル';
COMMENT ON COLUMN "public"."device_info"."computer_name" IS 'コンピュータ名';
COMMENT ON COLUMN "public"."device_info"."login_username" IS 'ログインユーザ名';
COMMENT ON COLUMN "public"."device_info"."project" IS '所属プロジェクト';
COMMENT ON COLUMN "public"."device_info"."dev_room" IS '所属開発室';
COMMENT ON COLUMN "public"."device_info"."job_number" IS '従業員番号（所属ユーザ、外部キー）';
COMMENT ON COLUMN "public"."device_info"."remark" IS '備考';
COMMENT ON COLUMN "public"."device_info"."self_confirm_id" IS '本人確認ID（辞書項目：CONFIRM_STATUS 関連）';
COMMENT ON COLUMN "public"."device_info"."os_id" IS 'OSID（辞書項目：OS_TYPE 関連）';
COMMENT ON COLUMN "public"."device_info"."memory_id" IS 'メモリID（辞書項目：MEMORY_SIZE 関連）';
COMMENT ON COLUMN "public"."device_info"."ssd_id" IS 'SSDID（辞書項目：SSD_SIZE 関連）';
COMMENT ON COLUMN "public"."device_info"."hdd_id" IS 'HDDID（辞書項目：HDD_SIZE 関連）';
COMMENT ON COLUMN "public"."device_info"."create_time" IS '作成日時';
COMMENT ON COLUMN "public"."device_info"."creater" IS '作成者';
COMMENT ON COLUMN "public"."device_info"."update_time" IS '更新日時';
COMMENT ON COLUMN "public"."device_info"."updater" IS '更新者';
COMMENT ON TABLE "public"."device_info" IS '機器情報テーブル（機器ハードウェア構成を保存）';

-- ----------------------------
-- Table structure for device_ip
-- ----------------------------
DROP TABLE IF EXISTS "public"."device_ip";
CREATE TABLE "public"."device_ip" (
  "ip_id" int4 NOT NULL,
  "device_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "ip_address" varchar(50) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "creater" varchar(100) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(100) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."device_ip"."ip_id" IS 'IP番号（プライマリキー）';
COMMENT ON COLUMN "public"."device_ip"."device_id" IS '機器番号（外部キー）';
COMMENT ON COLUMN "public"."device_ip"."ip_address" IS 'IPアドレス（ユニーク）';
COMMENT ON COLUMN "public"."device_ip"."create_time" IS '作成日時';
COMMENT ON COLUMN "public"."device_ip"."creater" IS '作成者';
COMMENT ON COLUMN "public"."device_ip"."update_time" IS '更新日時';
COMMENT ON COLUMN "public"."device_ip"."updater" IS '更新者';
COMMENT ON TABLE "public"."device_ip" IS '機器IPテーブル（機器に関連するIPアドレスを保存）';

-- ----------------------------
-- Table structure for device_permission
-- ----------------------------
DROP TABLE IF EXISTS "public"."device_permission";
CREATE TABLE "public"."device_permission" (
  "permission_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "device_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "domain_status_id" int8,
  "domain_group" varchar(100) COLLATE "pg_catalog"."default",
  "no_domain_reason" text COLLATE "pg_catalog"."default",
  "smartit_status_id" int8,
  "no_smartit_reason" text COLLATE "pg_catalog"."default",
  "usb_status_id" int8,
  "usb_reason" text COLLATE "pg_catalog"."default",
  "usb_expire_date" date,
  "antivirus_status_id" int8,
  "no_symantec_reason" text COLLATE "pg_catalog"."default",
  "remark" text COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "creater" varchar(100) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(100) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."device_permission"."permission_id" IS '権限番号（プライマリキー）';
COMMENT ON COLUMN "public"."device_permission"."device_id" IS '機器番号（外部キー：device_info 関連）';
COMMENT ON COLUMN "public"."device_permission"."domain_status_id" IS 'ドメイン状態ID（辞書項目：DOMAIN_STATUS 関連）';
COMMENT ON COLUMN "public"."device_permission"."domain_group" IS 'ドメイン内グループ名';
COMMENT ON COLUMN "public"."device_permission"."no_domain_reason" IS 'ドメイン未参加理由';
COMMENT ON COLUMN "public"."device_permission"."smartit_status_id" IS 'SmartIT状態ID（辞書項目：SMARTIT_STATUS 関連）';
COMMENT ON COLUMN "public"."device_permission"."no_smartit_reason" IS 'SmartIT未インストール理由';
COMMENT ON COLUMN "public"."device_permission"."usb_status_id" IS 'USB状態ID（辞書項目：USB_STATUS 関連）';
COMMENT ON COLUMN "public"."device_permission"."usb_reason" IS 'USB使用許可理由';
COMMENT ON COLUMN "public"."device_permission"."usb_expire_date" IS 'USB使用有効期限';
COMMENT ON COLUMN "public"."device_permission"."antivirus_status_id" IS 'antivirus状態ID（辞書項目：ANTIVIRUS_STATUS 関連）';
COMMENT ON COLUMN "public"."device_permission"."no_symantec_reason" IS 'Symantec未導入理由';
COMMENT ON COLUMN "public"."device_permission"."remark" IS '備考';
COMMENT ON COLUMN "public"."device_permission"."create_time" IS '作成日時';
COMMENT ON COLUMN "public"."device_permission"."creater" IS '作成者';
COMMENT ON COLUMN "public"."device_permission"."update_time" IS '更新日時';
COMMENT ON COLUMN "public"."device_permission"."updater" IS '更新者';
COMMENT ON TABLE "public"."device_permission" IS '機器使用権限テーブル（機器のドメイン、USB等の権限を保存：機器単位管理）';

-- ----------------------------
-- Table structure for dict
-- ----------------------------
DROP TABLE IF EXISTS "public"."dict";
CREATE TABLE "public"."dict" (
  "dict_id" int8 NOT NULL DEFAULT nextval('dict_dict_id_seq'::regclass),
  "dict_type_code" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_type_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_type_description" text COLLATE "pg_catalog"."default",
  "dict_item_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4 DEFAULT 0,
  "is_enabled" int2 NOT NULL DEFAULT 1,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "creater" varchar(100) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(100) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."dict"."dict_id" IS '辞書ID（プライマリキー）';
COMMENT ON COLUMN "public"."dict"."dict_type_code" IS '辞書タイプコード';
COMMENT ON COLUMN "public"."dict"."dict_type_name" IS '辞書タイプ名';
COMMENT ON COLUMN "public"."dict"."dict_type_description" IS '辞書タイプ説明';
COMMENT ON COLUMN "public"."dict"."dict_item_name" IS '辞書項目名';
COMMENT ON COLUMN "public"."dict"."sort" IS 'ソート番号';
COMMENT ON COLUMN "public"."dict"."is_enabled" IS '有効フラグ（0=無効、1=有効）';
COMMENT ON COLUMN "public"."dict"."create_time" IS '作成日時';
COMMENT ON COLUMN "public"."dict"."creater" IS '作成者';
COMMENT ON COLUMN "public"."dict"."update_time" IS '更新日時';
COMMENT ON COLUMN "public"."dict"."updater" IS '更新者';
COMMENT ON TABLE "public"."dict" IS '統合辞書テーブル（辞書タイプと項目を管理）';

-- ----------------------------
-- Table structure for monitor_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."monitor_info";
CREATE TABLE "public"."monitor_info" (
  "monitor_id" int4 NOT NULL,
  "device_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "monitor_name" varchar(100) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "creater" varchar(100) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(100) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."monitor_info"."monitor_id" IS 'モニター番号（プライマリキー）';
COMMENT ON COLUMN "public"."monitor_info"."device_id" IS '機器番号（外部キー）';
COMMENT ON COLUMN "public"."monitor_info"."monitor_name" IS 'モニター名';
COMMENT ON COLUMN "public"."monitor_info"."create_time" IS '作成日時';
COMMENT ON COLUMN "public"."monitor_info"."creater" IS '作成者';
COMMENT ON COLUMN "public"."monitor_info"."update_time" IS '更新日時';
COMMENT ON COLUMN "public"."monitor_info"."updater" IS '更新者';
COMMENT ON TABLE "public"."monitor_info" IS 'モニター情報テーブル（機器に関連するモニターを保存）';

-- ----------------------------
-- Table structure for sampling_check
-- ----------------------------
DROP TABLE IF EXISTS "public"."sampling_check";
CREATE TABLE "public"."sampling_check" (
  "sampling_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "job_number" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "device_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "installed_software" bool,
  "disposal_measures" text COLLATE "pg_catalog"."default",
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "report_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "update_date" date NOT NULL,
  "screen_saver_pwd" bool,
  "usb_interface" bool,
  "security_patch" bool,
  "antivirus_protection" bool,
  "boot_authentication" bool,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "creater" varchar(100) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(100) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."sampling_check"."sampling_id" IS 'サンプリングチェック番号（プライマリキー）';
COMMENT ON COLUMN "public"."sampling_check"."job_number" IS '従業員番号（外部キー）';
COMMENT ON COLUMN "public"."sampling_check"."device_id" IS '機器番号（外部キー）';
COMMENT ON COLUMN "public"."sampling_check"."installed_software" IS 'インストールソフトウェア';
COMMENT ON COLUMN "public"."sampling_check"."disposal_measures" IS '処置措置';
COMMENT ON COLUMN "public"."sampling_check"."name" IS '氏名';
COMMENT ON COLUMN "public"."sampling_check"."report_id" IS 'レポート番号';
COMMENT ON COLUMN "public"."sampling_check"."update_date" IS '更新日';
COMMENT ON COLUMN "public"."sampling_check"."screen_saver_pwd" IS 'スクリーンセーバーパスワード状態';
COMMENT ON COLUMN "public"."sampling_check"."usb_interface" IS 'USBインターフェース状態';
COMMENT ON COLUMN "public"."sampling_check"."security_patch" IS 'セキュリティパッチ状態';
COMMENT ON COLUMN "public"."sampling_check"."antivirus_protection" IS 'ウイルス防護状態ID';
COMMENT ON COLUMN "public"."sampling_check"."boot_authentication" IS '起動認証状態';
COMMENT ON COLUMN "public"."sampling_check"."create_time" IS '作成日時';
COMMENT ON COLUMN "public"."sampling_check"."creater" IS '作成者';
COMMENT ON COLUMN "public"."sampling_check"."update_time" IS '更新日時';
COMMENT ON COLUMN "public"."sampling_check"."updater" IS '更新者';
COMMENT ON TABLE "public"."sampling_check" IS 'サンプリングチェックテーブル（サンプリングチェック詳細を保存）';

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS "public"."users";
CREATE TABLE "public"."users" (
  "user_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "dept_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "job_number" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "user_type_id" int8 NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "creater" varchar(100) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updater" varchar(100) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."users"."user_id" IS 'ユーザID（プライマリキー）';
COMMENT ON COLUMN "public"."users"."dept_id" IS '部署番号';
COMMENT ON COLUMN "public"."users"."name" IS '氏名';
COMMENT ON COLUMN "public"."users"."job_number" IS '従業員番号（ユニーク、機器/権限関連）';
COMMENT ON COLUMN "public"."users"."user_type_id" IS 'ユーザタイプID（辞書項目：USER_TYPE 関連）';
COMMENT ON COLUMN "public"."users"."password" IS 'パスワード（暗号化保存）';
COMMENT ON COLUMN "public"."users"."create_time" IS '作成日時';
COMMENT ON COLUMN "public"."users"."creater" IS '作成者';
COMMENT ON COLUMN "public"."users"."update_time" IS '更新日時';
COMMENT ON COLUMN "public"."users"."updater" IS '更新者';
COMMENT ON TABLE "public"."users" IS 'ユーザテーブル（ユーザ基本情報を保存）';

-- ----------------------------
-- Function structure for update_modified_column
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."update_modified_column"();
CREATE FUNCTION "public"."update_modified_column"()
  RETURNS "pg_catalog"."trigger" AS $BODY$
=======
-- ============================================
-- Table 1: dict - 统合辞书表
-- ============================================
CREATE TABLE dict (
    dict_id int8 NOT NULL DEFAULT nextval('dict_dict_id_seq'::regclass),
    dict_type_code varchar(50)   NOT NULL,
    dict_type_name varchar(100)   NOT NULL,
    dict_type_description text  ,
    dict_item_name varchar(100)   NOT NULL,
    sort int4 DEFAULT 0,
    is_enabled int2 NOT NULL DEFAULT 1,
    create_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creater varchar(100)  ,
    update_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(100)  ,
    PRIMARY KEY (dict_id)
);

COMMENT ON TABLE dict IS '統合辞書テーブル（辞書タイプと項目を管理）';
COMMENT ON COLUMN dict.dict_id IS '辞書ID（プライマリキー）';
COMMENT ON COLUMN dict.dict_type_code IS '辞書タイプコード';
COMMENT ON COLUMN dict.dict_type_name IS '辞書タイプ名';
COMMENT ON COLUMN dict.dict_type_description IS '辞書タイプ説明';
COMMENT ON COLUMN dict.dict_item_name IS '辞書項目名';
COMMENT ON COLUMN dict.sort IS 'ソート番号';
COMMENT ON COLUMN dict.is_enabled IS '有効フラグ（0=無効、1=有効）';
COMMENT ON COLUMN dict.create_time IS '作成日時';
COMMENT ON COLUMN dict.creater IS '作成者';
COMMENT ON COLUMN dict.update_time IS '更新日時';
COMMENT ON COLUMN dict.updater IS '更新者';

-- ============================================
-- Table 2: "user" - 用户表
-- ============================================
CREATE TABLE "user" (
    user_id varchar(50)   NOT NULL,
    dept_id varchar(50)   NOT NULL,
    name varchar(100)   NOT NULL,
    job_number varchar(50)   NOT NULL,
    user_type_id int8 NOT NULL,
    password varchar(255)   NOT NULL,
    create_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creater varchar(100)  ,
    update_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(100)  ,
    PRIMARY KEY (user_id),
    CONSTRAINT uk_user_job_number UNIQUE (job_number),
    CONSTRAINT fk_user_type FOREIGN KEY (user_type_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE "user" IS 'ユーザテーブル（ユーザ基本情報を保存）';
COMMENT ON COLUMN "user".user_id IS 'ユーザID（プライマリキー）';
COMMENT ON COLUMN "user".dept_id IS '部署番号';
COMMENT ON COLUMN "user".name IS '氏名';
COMMENT ON COLUMN "user".job_number IS '従業員番号（ユニーク、機器/権限関連）';
COMMENT ON COLUMN "user".user_type_id IS 'ユーザタイプID（辞書項目：USER_TYPE 関連）';
COMMENT ON COLUMN "user".password IS 'パスワード（暗号化保存）';
COMMENT ON COLUMN "user".create_time IS '作成日時';
COMMENT ON COLUMN "user".creater IS '作成者';
COMMENT ON COLUMN "user".update_time IS '更新日時';
COMMENT ON COLUMN "user".updater IS '更新者';

-- ============================================
-- Table 3: device_info - 機器情報表
-- ============================================
CREATE TABLE device_info (
    device_id varchar(50)   NOT NULL,
    device_model varchar(100)  ,
    computer_name varchar(100)  ,
    login_username varchar(100)  ,
    project varchar(100)  ,
    dev_room varchar(100)  ,
    job_number varchar(50)   NOT NULL,
    remark text  ,
    self_confirm_id int8,
    os_id int8,
    memory_id int8,
    ssd_id int8,
    hdd_id int8,
    create_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creater varchar(100)  ,
    update_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(100)  ,
    PRIMARY KEY (device_id),
    CONSTRAINT fk_device_user FOREIGN KEY (job_number) REFERENCES "user"(job_number) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_device_self_confirm FOREIGN KEY (self_confirm_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_device_os FOREIGN KEY (os_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_device_memory FOREIGN KEY (memory_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_device_ssd FOREIGN KEY (ssd_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_device_hdd FOREIGN KEY (hdd_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE device_info IS '機器情報テーブル（機器ハードウェア構成を保存）';
COMMENT ON COLUMN device_info.device_id IS '機器番号（プライマリキー）';
COMMENT ON COLUMN device_info.device_model IS 'ホストモデル';
COMMENT ON COLUMN device_info.computer_name IS 'コンピュータ名';
COMMENT ON COLUMN device_info.login_username IS 'ログインユーザ名';
COMMENT ON COLUMN device_info.project IS '所属プロジェクト';
COMMENT ON COLUMN device_info.dev_room IS '所属開発室';
COMMENT ON COLUMN device_info.job_number IS '従業員番号（所属ユーザ、外部キー）';
COMMENT ON COLUMN device_info.remark IS '備考';
COMMENT ON COLUMN device_info.self_confirm_id IS '本人確認ID（辞書項目：CONFIRM_STATUS 関連）';
COMMENT ON COLUMN device_info.os_id IS 'OSID（辞書項目：OS_TYPE 関連）';
COMMENT ON COLUMN device_info.memory_id IS 'メモリID（辞書項目：MEMORY_SIZE 関連）';
COMMENT ON COLUMN device_info.ssd_id IS 'SSDID（辞書項目：SSD_SIZE 関連）';
COMMENT ON COLUMN device_info.hdd_id IS 'HDDID（辞書項目：HDD_SIZE 関連）';
COMMENT ON COLUMN device_info.create_time IS '作成日時';
COMMENT ON COLUMN device_info.creater IS '作成者';
COMMENT ON COLUMN device_info.update_time IS '更新日時';
COMMENT ON COLUMN device_info.updater IS '更新者';

-- ============================================
-- Table 4: device_ip - 機器IP表
-- ============================================
CREATE TABLE device_ip (
    ip_id int4 NOT NULL,
    device_id varchar(50)   NOT NULL,
    ip_address varchar(50)  ,
    create_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creater varchar(100)  ,
    update_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(100)  ,
    PRIMARY KEY (ip_id),
    CONSTRAINT uk_device_ip_address UNIQUE (ip_address),
    CONSTRAINT fk_device_ip FOREIGN KEY (device_id) REFERENCES device_info(device_id) ON DELETE CASCADE ON UPDATE CASCADE
);

COMMENT ON TABLE device_ip IS '機器IPテーブル（機器に関連するIPアドレスを保存）';
COMMENT ON COLUMN device_ip.ip_id IS 'IP番号（プライマリキー）';
COMMENT ON COLUMN device_ip.device_id IS '機器番号（外部キー）';
COMMENT ON COLUMN device_ip.ip_address IS 'IPアドレス（ユニーク）';
COMMENT ON COLUMN device_ip.create_time IS '作成日時';
COMMENT ON COLUMN device_ip.creater IS '作成者';
COMMENT ON COLUMN device_ip.update_time IS '更新日時';
COMMENT ON COLUMN device_ip.updater IS '更新者';

-- ============================================
-- Table 5: device_permission - 機器使用権限表
-- ============================================
CREATE TABLE device_permission (
    permission_id varchar(50)   NOT NULL,
    device_id varchar(50)   NOT NULL,
    domain_status_id int8,
    domain_group varchar(100)  ,
    no_domain_reason text  ,
    smartit_status_id int8,
    no_smartit_reason text  ,
    usb_status_id int8,
    usb_reason text  ,
    usb_expire_date date,
    antivirus_status_id int8,
    no_symantec_reason text  ,
    remark text  ,
    create_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creater varchar(100)  ,
    update_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(100)  ,
    PRIMARY KEY (permission_id),
    CONSTRAINT fk_permission_device FOREIGN KEY (device_id) REFERENCES device_info(device_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_permission_domain FOREIGN KEY (domain_status_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_permission_smartit FOREIGN KEY (smartit_status_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_permission_usb FOREIGN KEY (usb_status_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_permission_antivirus FOREIGN KEY (antivirus_status_id) REFERENCES dict(dict_id) ON DELETE SET NULL ON UPDATE CASCADE
);

COMMENT ON TABLE device_permission IS '機器使用権限テーブル（機器のドメイン、USB等の権限を保存：機器単位管理）';
COMMENT ON COLUMN device_permission.permission_id IS '権限番号（プライマリキー）';
COMMENT ON COLUMN device_permission.device_id IS '機器番号（外部キー：device_info 関連）';
COMMENT ON COLUMN device_permission.domain_status_id IS 'ドメイン状態ID（辞書項目：DOMAIN_STATUS 関連）';
COMMENT ON COLUMN device_permission.domain_group IS 'ドメイン内グループ名';
COMMENT ON COLUMN device_permission.no_domain_reason IS 'ドメイン未参加理由';
COMMENT ON COLUMN device_permission.smartit_status_id IS 'SmartIT状態ID（辞書項目：SMARTIT_STATUS 関連）';
COMMENT ON COLUMN device_permission.no_smartit_reason IS 'SmartIT未インストール理由';
COMMENT ON COLUMN device_permission.usb_status_id IS 'USB状態ID（辞書項目：USB_STATUS 関連）';
COMMENT ON COLUMN device_permission.usb_reason IS 'USB使用許可理由';
COMMENT ON COLUMN device_permission.usb_expire_date IS 'USB使用有効期限';
COMMENT ON COLUMN device_permission.antivirus_status_id IS 'antivirus状態ID（辞書項目：ANTIVIRUS_STATUS 関連）';
COMMENT ON COLUMN device_permission.no_symantec_reason IS 'Symantec未導入理由';
COMMENT ON COLUMN device_permission.remark IS '備考';
COMMENT ON COLUMN device_permission.create_time IS '作成日時';
COMMENT ON COLUMN device_permission.creater IS '作成者';
COMMENT ON COLUMN device_permission.update_time IS '更新日時';
COMMENT ON COLUMN device_permission.updater IS '更新者';

-- ============================================
-- Table 6: monitor_info - モニター情報表
-- ============================================
CREATE TABLE monitor_info (
    monitor_id int4 NOT NULL,
    device_id varchar(50)   NOT NULL,
    monitor_name varchar(100)  ,
    create_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creater varchar(100)  ,
    update_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(100)  ,
    PRIMARY KEY (monitor_id),
    CONSTRAINT fk_monitor_device FOREIGN KEY (device_id) REFERENCES device_info(device_id) ON DELETE CASCADE ON UPDATE CASCADE
);

COMMENT ON TABLE monitor_info IS 'モニター情報テーブル（機器に関連するモニターを保存）';
COMMENT ON COLUMN monitor_info.monitor_id IS 'モニター番号（プライマリキー）';
COMMENT ON COLUMN monitor_info.device_id IS '機器番号（外部キー）';
COMMENT ON COLUMN monitor_info.monitor_name IS 'モニター名';
COMMENT ON COLUMN monitor_info.create_time IS '作成日時';
COMMENT ON COLUMN monitor_info.creater IS '作成者';
COMMENT ON COLUMN monitor_info.update_time IS '更新日時';
COMMENT ON COLUMN monitor_info.updater IS '更新者';

-- ============================================
-- Table 7: sampling_check - サンプリングチェック表
-- ============================================
CREATE TABLE sampling_check (
    sampling_id varchar(50)   NOT NULL,
    job_number varchar(50)   NOT NULL,
    device_id varchar(50)   NOT NULL,
    installed_software bool,
    disposal_measures text  ,
    name varchar(100)   NOT NULL,
    report_id varchar(50)   NOT NULL,
    update_date date NOT NULL,
    screen_saver_pwd bool,
    usb_interface bool,
    security_patch bool,
    antivirus_protection bool,
    boot_authentication bool,
    create_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creater varchar(100)  ,
    update_time timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(100)  ,
    PRIMARY KEY (sampling_id),
    CONSTRAINT fk_sampling_check_user FOREIGN KEY (job_number) REFERENCES "user"(job_number) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_sampling_check_device FOREIGN KEY (device_id) REFERENCES device_info(device_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

COMMENT ON TABLE sampling_check IS 'サンプリングチェックテーブル（サンプリングチェック詳細を保存）';
COMMENT ON COLUMN sampling_check.sampling_id IS 'サンプリングチェック番号（プライマリキー）';
COMMENT ON COLUMN sampling_check.job_number IS '従業員番号（外部キー）';
COMMENT ON COLUMN sampling_check.device_id IS '機器番号（外部キー）';
COMMENT ON COLUMN sampling_check.installed_software IS 'インストールソフトウェア';
COMMENT ON COLUMN sampling_check.disposal_measures IS '処置措置';
COMMENT ON COLUMN sampling_check.name IS '氏名';
COMMENT ON COLUMN sampling_check.report_id IS 'レポート番号';
COMMENT ON COLUMN sampling_check.update_date IS '更新日';
COMMENT ON COLUMN sampling_check.screen_saver_pwd IS 'スクリーンセーバーパスワード状態';
COMMENT ON COLUMN sampling_check.usb_interface IS 'USBインターフェース状態';
COMMENT ON COLUMN sampling_check.security_patch IS 'セキュリティパッチ状態';
COMMENT ON COLUMN sampling_check.antivirus_protection IS 'ウイルス防護状態ID';
COMMENT ON COLUMN sampling_check.boot_authentication IS '起動認証状態';
COMMENT ON COLUMN sampling_check.create_time IS '作成日時';
COMMENT ON COLUMN sampling_check.creater IS '作成者';
COMMENT ON COLUMN sampling_check.update_time IS '更新日時';
COMMENT ON COLUMN sampling_check.updater IS '更新者';

-- ============================================
-- Function: update_modified_column
-- ============================================
CREATE FUNCTION update_modified_column()
RETURNS trigger AS $BODY$
>>>>>>> 0fcbd7ec8d221abe7008f62c49eb74283fbdb4d4
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$BODY$
<<<<<<< HEAD
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."dict_dict_id_seq"
OWNED BY "public"."dict"."dict_id";
SELECT setval('"public"."dict_dict_id_seq"', 21, true);

-- ----------------------------
-- Indexes structure for table device_info
-- ----------------------------
CREATE INDEX "idx_device_hdd" ON "public"."device_info" USING btree (
  "hdd_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_device_memory" ON "public"."device_info" USING btree (
  "memory_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_device_os" ON "public"."device_info" USING btree (
  "os_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_device_self_confirm" ON "public"."device_info" USING btree (
  "self_confirm_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_device_ssd" ON "public"."device_info" USING btree (
  "ssd_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_device_user" ON "public"."device_info" USING btree (
  "job_number" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table device_info
-- ----------------------------
CREATE TRIGGER "trigger_update_device_info" BEFORE UPDATE ON "public"."device_info"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_modified_column"();

-- ----------------------------
-- Primary Key structure for table device_info
-- ----------------------------
ALTER TABLE "public"."device_info" ADD CONSTRAINT "device_info_pkey" PRIMARY KEY ("device_id");

-- ----------------------------
-- Indexes structure for table device_ip
-- ----------------------------
CREATE INDEX "idx_device_ip" ON "public"."device_ip" USING btree (
  "device_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table device_ip
-- ----------------------------
CREATE TRIGGER "trigger_update_device_ip" BEFORE UPDATE ON "public"."device_ip"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_modified_column"();

-- ----------------------------
-- Uniques structure for table device_ip
-- ----------------------------
ALTER TABLE "public"."device_ip" ADD CONSTRAINT "uk_device_ip_address" UNIQUE ("ip_address");

-- ----------------------------
-- Primary Key structure for table device_ip
-- ----------------------------
ALTER TABLE "public"."device_ip" ADD CONSTRAINT "device_ip_pkey" PRIMARY KEY ("ip_id");

-- ----------------------------
-- Indexes structure for table device_permission
-- ----------------------------
CREATE INDEX "idx_permission_antivirus" ON "public"."device_permission" USING btree (
  "antivirus_status_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_permission_device" ON "public"."device_permission" USING btree (
  "device_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_permission_domain" ON "public"."device_permission" USING btree (
  "domain_status_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_permission_smartit" ON "public"."device_permission" USING btree (
  "smartit_status_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_permission_usb" ON "public"."device_permission" USING btree (
  "usb_status_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table device_permission
-- ----------------------------
CREATE TRIGGER "trigger_update_device_permission" BEFORE UPDATE ON "public"."device_permission"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_modified_column"();

-- ----------------------------
-- Primary Key structure for table device_permission
-- ----------------------------
ALTER TABLE "public"."device_permission" ADD CONSTRAINT "device_permission_pkey" PRIMARY KEY ("permission_id");

-- ----------------------------
-- Triggers structure for table dict
-- ----------------------------
CREATE TRIGGER "trigger_update_dict" BEFORE UPDATE ON "public"."dict"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_modified_column"();

-- ----------------------------
-- Primary Key structure for table dict
-- ----------------------------
ALTER TABLE "public"."dict" ADD CONSTRAINT "dict_pkey" PRIMARY KEY ("dict_id");

-- ----------------------------
-- Indexes structure for table monitor_info
-- ----------------------------
CREATE INDEX "idx_monitor_device" ON "public"."monitor_info" USING btree (
  "device_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table monitor_info
-- ----------------------------
CREATE TRIGGER "trigger_update_monitor_info" BEFORE UPDATE ON "public"."monitor_info"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_modified_column"();

-- ----------------------------
-- Primary Key structure for table monitor_info
-- ----------------------------
ALTER TABLE "public"."monitor_info" ADD CONSTRAINT "monitor_info_pkey" PRIMARY KEY ("monitor_id");

-- ----------------------------
-- Primary Key structure for table sampling_check
-- ----------------------------
ALTER TABLE "public"."sampling_check" ADD CONSTRAINT "sampling_check_pkey" PRIMARY KEY ("sampling_id");

-- ----------------------------
-- Indexes structure for table users
-- ----------------------------
CREATE INDEX "idx_user_type" ON "public"."users" USING btree (
  "user_type_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table users
-- ----------------------------
CREATE TRIGGER "trigger_update_user" BEFORE UPDATE ON "public"."users"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_modified_column"();

-- ----------------------------
-- Uniques structure for table users
-- ----------------------------
ALTER TABLE "public"."users" ADD CONSTRAINT "uk_user_job_number" UNIQUE ("job_number");

-- ----------------------------
-- Primary Key structure for table users
-- ----------------------------
ALTER TABLE "public"."users" ADD CONSTRAINT "users_pkey" PRIMARY KEY ("user_id");

-- ----------------------------
-- Foreign Keys structure for table device_info
-- ----------------------------
ALTER TABLE "public"."device_info" ADD CONSTRAINT "fk_device_hdd" FOREIGN KEY ("hdd_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "public"."device_info" ADD CONSTRAINT "fk_device_memory" FOREIGN KEY ("memory_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "public"."device_info" ADD CONSTRAINT "fk_device_os" FOREIGN KEY ("os_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "public"."device_info" ADD CONSTRAINT "fk_device_self_confirm" FOREIGN KEY ("self_confirm_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "public"."device_info" ADD CONSTRAINT "fk_device_ssd" FOREIGN KEY ("ssd_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "public"."device_info" ADD CONSTRAINT "fk_device_user" FOREIGN KEY ("job_number") REFERENCES "public"."users" ("job_number") ON DELETE RESTRICT ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table device_ip
-- ----------------------------
ALTER TABLE "public"."device_ip" ADD CONSTRAINT "fk_device_ip" FOREIGN KEY ("device_id") REFERENCES "public"."device_info" ("device_id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table device_permission
-- ----------------------------
ALTER TABLE "public"."device_permission" ADD CONSTRAINT "fk_permission_antivirus" FOREIGN KEY ("antivirus_status_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "public"."device_permission" ADD CONSTRAINT "fk_permission_device" FOREIGN KEY ("device_id") REFERENCES "public"."device_info" ("device_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "public"."device_permission" ADD CONSTRAINT "fk_permission_domain" FOREIGN KEY ("domain_status_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "public"."device_permission" ADD CONSTRAINT "fk_permission_smartit" FOREIGN KEY ("smartit_status_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "public"."device_permission" ADD CONSTRAINT "fk_permission_usb" FOREIGN KEY ("usb_status_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table monitor_info
-- ----------------------------
ALTER TABLE "public"."monitor_info" ADD CONSTRAINT "fk_monitor_device" FOREIGN KEY ("device_id") REFERENCES "public"."device_info" ("device_id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table sampling_check
-- ----------------------------
ALTER TABLE "public"."sampling_check" ADD CONSTRAINT "fk_sampling_check_device" FOREIGN KEY ("device_id") REFERENCES "public"."device_info" ("device_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."sampling_check" ADD CONSTRAINT "fk_sampling_check_user" FOREIGN KEY ("job_number") REFERENCES "public"."users" ("job_number") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table users
-- ----------------------------
ALTER TABLE "public"."users" ADD CONSTRAINT "fk_user_type" FOREIGN KEY ("user_type_id") REFERENCES "public"."dict" ("dict_id") ON DELETE SET NULL ON UPDATE CASCADE;
=======
LANGUAGE plpgsql VOLATILE
COST 100;

-- ============================================
-- Triggers
-- ============================================
CREATE TRIGGER trigger_update_device_info BEFORE UPDATE ON device_info
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TRIGGER trigger_update_device_ip BEFORE UPDATE ON device_ip
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TRIGGER trigger_update_device_permission BEFORE UPDATE ON device_permission
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TRIGGER trigger_update_dict BEFORE UPDATE ON dict
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TRIGGER trigger_update_monitor_info BEFORE UPDATE ON monitor_info
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TRIGGER trigger_update_user BEFORE UPDATE ON "user"
FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

-- ============================================
-- Indexes
-- ============================================
-- dict table indexes

-- user table indexes
CREATE INDEX idx_user_type ON "user" USING btree (user_type_id);

-- device_info table indexes
CREATE INDEX idx_device_hdd ON device_info USING btree (hdd_id);
CREATE INDEX idx_device_memory ON device_info USING btree (memory_id);
CREATE INDEX idx_device_os ON device_info USING btree (os_id);
CREATE INDEX idx_device_self_confirm ON device_info USING btree (self_confirm_id);
CREATE INDEX idx_device_ssd ON device_info USING btree (ssd_id);
CREATE INDEX idx_device_user ON device_info USING btree (job_number);

-- device_ip table indexes
CREATE INDEX idx_device_ip ON device_ip USING btree (device_id);

-- device_permission table indexes
CREATE INDEX idx_permission_antivirus ON device_permission USING btree (antivirus_status_id);
CREATE INDEX idx_permission_device ON device_permission USING btree (device_id);
CREATE INDEX idx_permission_domain ON device_permission USING btree (domain_status_id);
CREATE INDEX idx_permission_smartit ON device_permission USING btree (smartit_status_id);
CREATE INDEX idx_permission_usb ON device_permission USING btree (usb_status_id);

-- monitor_info table indexes
CREATE INDEX idx_monitor_device ON monitor_info USING btree (device_id);

-- sampling_check table indexes

-- ============================================
-- Alter sequences owned by
-- ============================================
ALTER SEQUENCE dict_dict_id_seq OWNED BY dict.dict_id;
SELECT setval('dict_dict_id_seq', 21, true);


-- ============================================
-- End of Schema Creation Script
-- ============================================
>>>>>>> 0fcbd7ec8d221abe7008f62c49eb74283fbdb4d4
