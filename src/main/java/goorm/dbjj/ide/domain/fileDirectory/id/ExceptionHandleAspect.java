package goorm.dbjj.ide.domain.fileDirectory.id;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static goorm.dbjj.ide.storageManager.StorageManager.RESOURCE_SEPARATOR;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ExceptionHandleAspect {

    @Value("${app.efs-root-directory}")
    private String ROOT_DIRECTORY;

    private String getRelativePath(String fullPath) {
        if (fullPath != null && fullPath.startsWith(ROOT_DIRECTORY)) {
            String withoutRoot = fullPath.substring(ROOT_DIRECTORY.length());
            return withoutRoot.startsWith(RESOURCE_SEPARATOR) ? // root 가 '/' 로 시작하는지 확인
                    withoutRoot.substring(withoutRoot.indexOf(RESOURCE_SEPARATOR, 1)) : withoutRoot;
        }
        return fullPath;
    }

    //CustomIOException 을 상속받은 예외들을 처리하는 Aspect

    //포인트컷 지정자?
    @Around("execution(* goorm.dbjj.ide.domain.fileDirectory.id.IdManagedProjectFileService.*(..))")
    public Object handleCustomIOException(ProceedingJoinPoint joinPoint) {
        log.trace(joinPoint.getSignature().getName());

        try {
            return joinPoint.proceed();

        } catch (CustomIOException e) {
            throw new BaseException(e.getModel().getMessage() + " Path: " + getRelativePath(e.getModel().getPath()));
        } catch (Throwable throwable) {
            throw new BaseException(throwable.getMessage());
        }
    }
}

