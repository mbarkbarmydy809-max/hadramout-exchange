import { Router, type IRouter } from "express";
import { existsSync } from "node:fs";
import path from "node:path";

const router: IRouter = Router();

const apkRelativePath = "android/app/build/outputs/apk/release/app-release.apk";
const apkCandidates = [
  path.resolve(process.cwd(), apkRelativePath),
  path.resolve(process.cwd(), "../..", apkRelativePath),
  path.resolve(process.cwd(), "..", apkRelativePath),
];

router.get("/download/app-release.apk", (req, res) => {
  const apkPath = apkCandidates.find((candidate) => existsSync(candidate));

  if (!apkPath) {
    req.log.error({ apkCandidates }, "APK file is not available");
    res.status(404).json({ message: "APK file is not available." });
    return;
  }

  res.download(apkPath, "app-release.apk", {
    headers: {
      "Content-Type": "application/vnd.android.package-archive",
      "Cache-Control": "public, max-age=3600",
    },
  });
});

export default router;