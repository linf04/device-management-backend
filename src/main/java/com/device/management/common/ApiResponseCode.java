package com.device.management.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * レスポンスステータスコード列挙型
 */
@Getter
@AllArgsConstructor
public enum ApiResponseCode {

    /* ===== パスワード変更業務コード ===== */
    PASSWORD_CHANGED_SUCCESS(20000, "パスワードが更新されました。再度ログインしてください。"),
    WRONG_CURRENT_PASSWORD(40001, "現在のパスワードが正しくありません"),
    WEAK_NEW_PASSWORD(40002, "新しいパスワードが強度要件を満たしていません"),
    PASSWORD_SAME_AS_OLD(40003, "新しいパスワードは古いパスワードと同じにすることはできません"),

    /* ===== 通用権限コード ==== */
    FORBIDDEN(403, "操作に対する権限がありません"),

    // --------------------------成功状態コード--------------------------
    SUCCESS(200, "成功"),
    LOGIN_SUCCESS(200, "ログイン成功"),
    LOGOUT_SUCCESS(200, "ログアウト成功"),

    // -------------------------- 認証関連エラー（401） --------------------------
    USER_NOT_FOUND(401, "ユーザーが存在しません"),
    PASSWORD_ERROR(401, "パスワードが正しくありません"),
    TOKEN_INVALID(401, "トークンが無効です"),
    TOKEN_EXPIRED(401, "トークンの有効期限が切れています"),
    UNAUTHORIZED(401, "未授权"),
    NOT_FOUND(404, "リソースが見つかりません"),

    // -------------------------- 業務エラー --------------------------
    PARAM_ERROR(400, "パラメータエラー"),
    FAIL(500, "システムエラー"),

    SYSTEM_ERROR(50000, "システムエラーが発生しました"),
    DICT_PARAM_ERROR(40001, "dictTypeCodeは文字列で指定してください");

    private final int code;       // HTTPステータスコード
    private final String message; // レスポンスメッセージ
}
